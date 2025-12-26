// typescript
export class ChatSession {

    private static _instance: ChatSession = new ChatSession()

    private currentChatId: string | null = null
    private listeners = new Set<(id: string | null) => Promise<void>>()
    private readonly paramName = 'chatId'
    private readonly supportsHistory = typeof window !== 'undefined' && typeof window.history !== 'undefined'

    private constructor() {
        // initialize from URL if available
        if (typeof window !== 'undefined') {
            const url = new URL(window.location.href)
            const id = url.searchParams.get(this.paramName)
            if (id) this.currentChatId = id
        }
    }

    static get instance(): ChatSession {
        return ChatSession._instance
    }

    get id(): string | null {
        return this.currentChatId
    }

    /**
     * Set the current chat id. If no id is provided a new UUID-like id is generated.
     * When the id changes the browser URL is updated. Set `push` to false to use replaceState.
     */
    async setId(id?: string, push = true): Promise<string> {
        // TODO remove the id generation logic and enforce passing an id
        const newId = id ?? (typeof crypto !== 'undefined' && 'randomUUID' in crypto
            ? (crypto as any).randomUUID()
            : `${Date.now()}-${Math.random().toString(36).slice(2)}`)
        this.currentChatId = newId
        this.updateUrl(newId, push)
        await this.emitChange()
        return newId
    }

    async clear(push = true) {
        this.currentChatId = null
        this.updateUrl(null, push)
        await this.emitChange()
    }

    async onChange(cb: (id: string | null) => Promise<void>): Promise<() => void> {
        this.listeners.add(cb)
        // call immediately with current value
        await cb(this.currentChatId)

        return () => this.listeners.delete(cb)
    }

    private async emitChange(): Promise<void> {
        for (const cb of this.listeners) {
            await cb(this.currentChatId)
        }
    }

    private updateUrl(id: string | null, push: boolean): void {
        if (!this.supportsHistory) return
        try {
            const url = new URL(window.location.href)
            if (id) {
                url.searchParams.set(this.paramName, id)
            } else {
                url.searchParams.delete(this.paramName)
            }

            const method = push ? window.history.pushState.bind(window.history) : window.history.replaceState.bind(window.history)
            method({}, '', url.toString())
        } catch {
            // ignore URL update failures
        }
    }
}

export const ChatSessionSingleton = ChatSession.instance
