import React, { useRef, useEffect, useMemo } from 'react'
import './App.css'
import Chat from './components/chat/Chat'
import { ChatController } from './services/chat-service/ChatController'
import type { ChatHandle } from './components/chat/Chat'
import SideDrawer from "./components/side-drawer/SideDrawer";

export default function App() {
  const chatRef = useRef<ChatHandle | null>(null)
  const controller = useMemo(() => new ChatController('http://localhost:8080'), [])

  useEffect(() => {
    controller.attach(chatRef)
    return () => controller.detach()
  }, [controller])

    //<Chat ref={chatRef} onSend={controller.onSend} />
  return (
    <div>
        <SideDrawer>
            <Chat ref={chatRef} onSend={controller.onSend} />
        </SideDrawer>
    </div>
  )
}