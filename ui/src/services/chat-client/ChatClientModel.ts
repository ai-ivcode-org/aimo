export interface NewChatResponse {
    chatId: string
}

export interface ChatRequest {
    message: string,
    stream?: boolean
}

export interface ChatMessage {
    id: number
    role: 'USER' | 'ASSISTANT' | 'SYSTEM' | 'TOOL'
    response?: string
    thinking?: string
    timestamp?: number
    done: boolean
}


export interface Callback {
    onMessage: (message: ChatMessage) => void
}

export interface ChatClient {
    newChat: () => Promise<NewChatResponse>
    chat: (chatId: string, request: ChatRequest, callback?: Callback) => Promise<ChatMessage>
    history: (chatId: string) => Promise<ChatMessage[]>
}