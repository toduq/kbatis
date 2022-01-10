package dev.todaka.kbatis.core

import java.lang.reflect.Type

/**
 * Extracted query string and arguments.
 */
data class KStatement(
    val sql: String,
    val args: List<Arg>,
) {
    data class Arg(
        val value: Any,
        val type: Type,
    )

    companion object {
        fun build(sql: String, args: List<Any>): KStatement {
            return KStatement(
                sql, args.map { Arg(it, it.javaClass) }
            )
        }
    }
}
