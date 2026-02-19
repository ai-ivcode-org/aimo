package org.ivcode.ai.ollama.client

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import java.time.Instant
import kotlin.time.Duration

class DurationSerializer : JsonSerializer<Duration>() {
    override fun serialize(value: Duration, gen: JsonGenerator, serializers: SerializerProvider) {
        gen.writeString(value.toString()) // outputs "32s", "1m", etc.
    }
}

class DurationDeserializer : JsonDeserializer<Duration>() {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): Duration {
        val text = p.text
        return Duration.parse(text) // parses "32s", "1m", etc.
    }
}

class InstantSerializer : JsonSerializer<Instant>() {
    override fun serialize(value: Instant, gen: JsonGenerator, serializers: SerializerProvider) {
        // Write Instant in ISO-8601 format (e.g. 2026-02-10T05:49:12.5858417Z)
        gen.writeString(value.toString())
    }

}

class InstantDeserializer : JsonDeserializer<Instant>() {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): Instant {
        val text = p.text
        // java.time.Instant.parse supports ISO-8601 strings with fractional seconds
        return Instant.parse(text)
    }
}