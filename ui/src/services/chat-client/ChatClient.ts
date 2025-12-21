export interface NewChatResponse {
    chatId: string
}

export interface ChatRequest {
    message: string,
    stream?: boolean
}

export interface ChatResponse {
    id: number
    // TODO make role an enum?
    role: string
    response: string
    thinking: string
    timestamp: number
    done: boolean
}

export interface Callback {
    onMessage: (message: ChatResponse) => void
}

export interface ChatClient {
    newChat: () => Promise<NewChatResponse>
    chat: (chatId: string, request: ChatRequest, callback?: Callback) => Promise<ChatResponse>
    history: (chatId: string) => Promise<ChatResponse[]>
}

export const ChatClientFactory = (
    baseUrl: string
): ChatClient => {
    // TODO make sure the baseUrl does not have a trailing slash

    const newChat = async (): Promise<NewChatResponse> => {
        // POST /chat/new
        const method = 'POST'
        const url = `${baseUrl}/chat/new`

        const res = await fetch(url, {
            method: method,
            headers: { 'Content-Type': 'application/json' }
        })

        if(!res.ok) {
            throw new Error(`Failed to create new chat: ${res.status} ${res.statusText}`)
        }

        const txt = await res.text()
        const parsed = JSON.parse(txt)

        return parsed as NewChatResponse
    }

    const chat = async (
        chatId: string,
        request: ChatRequest,
        callback?: Callback
    ): Promise<ChatResponse> => {
        // POST /chat/{chatId}
        const method = 'POST'
        const url = `${baseUrl}/chat/${chatId}`

        if(!chatId) {
            throw new Error('chatId is required')
        }

        const res = await fetch(url, {
            method: method,
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(request)
        })

        if (!res.body) {
            // No stream support; try to parse whole body as JSON
            const txt = await res.text()

            const parsed = JSON.parse(txt)

            return parsed as ChatResponse
        }

        const reader = res.body.getReader()
        const decoder = new TextDecoder()
        let buffer = ''
        let lastEvent: ChatResponse = null;

        const emitJson = (jsonStr: string) => {
            if (!jsonStr) return
            try {
                const response = JSON.parse(jsonStr) as ChatResponse

                lastEvent = response
                callback?.onMessage(response)
            } catch {
                // TODO add an error callback?
                // If it isn't a structured MessageEvent, send raw string
                const ev: ChatResponse = { id: -1, role: "", response: jsonStr, thinking: '', done: true, timestamp: Date.now()}

                lastEvent = ev
                callback?.onMessage(ev)
            }
        }

        // Read stream chunks and attempt two strategies:
        // 1) NDJSON / newline-delimited objects
        // 2) Concatenated JSON objects using brace counting with basic string/escape handling
        let done = false
        while (!done) {
            const { value, done: streamDone } = await reader.read()
            done = !!streamDone
            buffer += value ? decoder.decode(value, { stream: true }) : ''

            // Handle newline-delimited objects first
            let nlIndex: number
            while ((nlIndex = buffer.indexOf('\n')) >= 0) {
                const line = buffer.slice(0, nlIndex).trim()
                buffer = buffer.slice(nlIndex + 1)
                if (line) emitJson(line)
            }

            // Attempt to extract concatenated objects from remaining buffer
            // Basic state machine to handle strings and escapes so braces inside strings don't break parsing
            let braceDepth = 0
            let inString = false
            let escape = false
            let start = -1
            for (let i = 0; i < buffer.length; i++) {
                const ch = buffer[i]
                if (escape) {
                    escape = false
                    continue
                }
                if (ch === '\\') {
                    escape = true
                    continue
                }
                if (ch === '"' ) {
                    inString = !inString
                    continue
                }
                if (inString) continue
                if (ch === '{') {
                    if (braceDepth === 0) start = i
                    braceDepth++
                } else if (ch === '}') {
                    braceDepth--
                    if (braceDepth === 0 && start >= 0) {
                        const objStr = buffer.slice(start, i + 1)
                        emitJson(objStr)
                        buffer = buffer.slice(i + 1)
                        // reset scanner to beginning of new buffer
                        i = -1
                        start = -1
                    }
                }
            }
        }

        // After stream end, try to parse any leftover content
        const leftover = buffer.trim()
        if (leftover) {
            // Try newline split first, then fallback to single JSON parse
            const parts = leftover.split('\n').map(p => p.trim()).filter(Boolean)
            if (parts.length > 1) {
                parts.forEach(emitJson)
            } else {
                emitJson(leftover)
            }
        }

        return lastEvent
    }

    const history = async (
        chatId: string
    ): Promise<ChatResponse[]> => {
        // GET /chat/{chatId}/history
        const method = 'GET'
        const url = `${baseUrl}/chat/${chatId}/history`

        const res = await fetch(url, {
            method: method,
            headers: { 'Content-Type': 'application/json' }
        })

        if(!res.ok) {
            throw new Error(`Failed to fetch chat history: ${res.status} ${res.statusText}`)
        }

        const txt = await res.text()
        const parsed = JSON.parse(txt)
        return parsed as ChatResponse[]
    }

    return {
        newChat,
        chat,
        history
    }
}