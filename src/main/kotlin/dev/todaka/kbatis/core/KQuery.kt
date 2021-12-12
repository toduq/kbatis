package dev.todaka.kbatis.core

/**
 * Extracted query string and arguments.
 */
data class KQuery(
    val sql: String,
    val args: List<String>,
)
