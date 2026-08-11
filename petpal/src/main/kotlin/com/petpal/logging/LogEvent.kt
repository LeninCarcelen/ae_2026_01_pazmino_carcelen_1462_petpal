package com.petpal.logging

object LogEvent {
    fun format(event: String, msg: String, vararg fields: Pair<String, Any?>): String {
        val extra = fields.joinToString(" | ") { (k, v) -> "$k=${safe(v)}" }
        val base = "event=$event | msg=$msg"
        return if (extra.isBlank()) base else "$base | $extra"
    }

    private fun safe(value: Any?): String {
        val text = (value?.toString() ?: "null").replace("|", "/")
        return if (text.contains(' ')) "\"$text\"" else text
    }
}
