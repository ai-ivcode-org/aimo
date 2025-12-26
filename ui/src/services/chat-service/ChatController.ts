// typescript
import {ChatClientFactory} from '../chat-client/ChatClient'
import {ChatMessage, ChatRequest, ChatClient} from '../chat-client/ChatClientModel'
import type {ChatHandle} from '../../components/chat/Chat'
//import type {Message} from '../../components/chat/ChatModel'
import React from "react";
import {ChatSessionSingleton} from "./ChatSession";

export class ChatController {
    private readonly client: ChatClient = ChatClientFactory('')
    private chatHandle?: React.RefObject<ChatHandle> | null

    private unsubscribeSessionChange: () => void | null = null

    constructor(baseUrl?: string) {
        if (baseUrl) this.client = ChatClientFactory(baseUrl)
    }

    async attach(chatHandle: React.RefObject<ChatHandle>) {
        this.chatHandle = chatHandle
        this.unsubscribeSessionChange = await ChatSessionSingleton.onChange(async (id: string | null) => {
            if (!id) {
                return
            }

            const inputEnabled = this.chatHandle?.current?.isInputEnabled() ?? true
            this.chatHandle?.current.setInputEnabled(false)

            try {
                const messages = await this.client.history(id)
                this.chatHandle?.current?.setMessages(messages)
            } finally {
                this.chatHandle?.current.setInputEnabled(inputEnabled)
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
            try {
                return await this.client.chat(id, reqFallback)
            } catch {
                return undefined
            }
        }

        const req: ChatRequest = {message: userMsg.response, stream: true}
        let isFirstChunk = true
        try {
            // always append incoming chunks to the placeholder message we created above
            return await this.client.chat(id, req, {
                onMessage: (ev: ChatMessage) => {
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
        }
    }
}