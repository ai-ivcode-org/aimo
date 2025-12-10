package org.ivcode.common.data.session

import org.ivcode.common.PROPERTY_PREFIX_APP_NAME

const val PROPERTY_NAME_SESSION_TYPE = "$PROPERTY_PREFIX_APP_NAME.session.type"

interface SessionFactory {
    fun <T> createCache(name: String, type: Class<T>): Session<T>
}