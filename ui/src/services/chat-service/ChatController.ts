// typescript
import {ChatClientFactory} from '../chat-client/ChatClient'
import {ChatMessage, ChatRequest, ChatClient} from '../chat-client/ChatClient'
import type {ChatHandle} from '../../components/chat/Chat'
import React from "react";
import {ChatSessionSingleton} from "./ChatSession";

export class ChatController {
    private readonly client: ChatClient = ChatClientFactory('')
    private chatHandle?: React.RefObject<ChatHandle> | null

    private unsubscribeSessionChange: () => void | null = null

    constructor(baseUrl?: string) {
        if (baseUrl) this.client = ChatClientFactory(baseUrl)
    }

    attach(chatHandle: React.RefObject<ChatHandle>) {
        if(this.chatHandle) {
            return
        }
        if(this.unsubscribeSessionChange) {
            this.unsubscribeSessionChange()
        }

        this.chatHandle = chatHandle
        this.unsubscribeSessionChange = ChatSessionSingleton.onChange(async (id: string | null) => {
            if (!id) {
                return
            }

            const enableInput = this.chatHandle?.current?.disableInput()
            try {
                const messages = await this.client.history(id)
                this.chatHandle?.current?.setMessages(messages)
            } finally {
                enableInput()
            }
        })
    }

    detach() {
        this.chatHandle = undefined

        if(this.unsubscribeSessionChange) {
            this.unsubscribeSessionChange()
            this.unsubscribeSessionChange = null
        }
    }

    /**
     * Pass this method to the Chat component as `onSend`.
     * It:
     * - inserts an assistant placeholder message,
     * - calls the API with `stream: true`,
     * - appends streamed chunks to the placeholder via the Chat imperative API.
     */
    onSend = async (userMsg: ChatMessage): Promise<ChatMessage | undefined> => {
        let id = ChatSessionSingleton.id
        if(!id) {
            const newChat = await this.client.newChat()

            // TODO: I need all listeners to finish before proceeding
            id = await ChatSessionSingleton.setId(newChat.chatId)
            this.chatHandle?.current?.addMessage(userMsg)
        }

        const handle = this.chatHandle?.current
        if (!handle) {
            // no UI attached; still call API but cannot update UI
            const reqFallback: ChatRequest = {message: userMsg.response, stream: false}

            const unsetBusy = this.chatHandle?.current?.busy()
            const enableInput = this.chatHandle?.current?.disableInput()
            try {
                return await this.client.chat(id, reqFallback)
            } catch {
                return undefined
            } finally {
                unsetBusy()
                enableInput()
            }
        }

        const req: ChatRequest = {message: userMsg.response, stream: true}
        let isFirstChunk = true
        const unsetBusy = this.chatHandle?.current?.busy()
        const enableInput = this.chatHandle?.current?.disableInput()
        try {
            // always append incoming chunks to the placeholder message we created above
            return await this.client.chat(id, req, {
                onMessage: (ev: ChatMessage) => {
                    unsetBusy()

                    if (isFirstChunk) {
                        isFirstChunk = false
                        handle.addMessage(ev)
                    } else {
                        handle.appendMessage(ev)
                    }
                }
            })
        } catch (err) {
            // show error text in the assistant message
            const errText = typeof err === 'string' ? err : (err instanceof Error ? err.message : 'Error')
            try {
                handle.appendMessage({
                    id: Date.now(),
                    response: `\n\n[Error] ${errText}`,
                    role: 'SYSTEM',
                    timestamp: Date.now(),
                    done: true
                })
            } catch {
                // ignore
            }
            throw err
        } finally {
            unsetBusy()
            enableInput()
        }
    }
}