package org.ivcode.ai.plugin.api.chat.controller

import org.ivcode.ai.plugin.api.chat.session.ChatSessionSystemMessage
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.util.UUID

internal class SystemMessageGetter<T> (
    private val receiver: T,
    private val getter: (T) -> String?,
): ChatSessionSystemMessage {
    override fun invoke(chatId: UUID): String? {
        return getter(receiver)
    }
}

internal class MethodGetter<T> (
    private val method: Method,
): (T) -> String? {
    override fun invoke(p1: T): String? {
        return method.invoke(p1) as String?
    }
}

internal class FieldGetter<T> (
    private val field: Field,
): (T) -> String? {
    override fun invoke(p1: T): String? {
        return field.get(p1) as String?
    }
}