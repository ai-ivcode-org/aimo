import {ChatMessage} from "../../services/chat-client/ChatClientModel";

export interface Message {
    message: ChatMessage
    expandThinking: boolean
}