package dev.todaka.kbatis.core

import java.sql.PreparedStatement
import java.sql.Types

/**
 * Type conversion between Object -> PreparedStatement, ResultSet -> Object.
 */
class TypeHandler {
    fun setParameter(ps: PreparedStatement, i: Int, parameter: Any?) {
        when (parameter) {
            null -> {
                ps.setNull(i, Types.VARCHAR)
            }
            is Int -> {
                ps.setInt(i, parameter)
            }
            is Long -> {
                ps.setLong(i, parameter)
            }
            is String -> {
                ps.setString(i, parameter)
            }
        }
    }
}
