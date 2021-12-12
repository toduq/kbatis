package dev.todaka.kbatis.driver

import dev.todaka.kbatis.core.KQuery
import dev.todaka.kbatis.core.TypeHandler
import java.sql.Connection
import java.sql.ResultSet

object QueryExecutor {
    fun execute(conn: Connection, query: KQuery): ResultSet {
        val ps = conn.prepareStatement(query.sql)
        val typeHandler = TypeHandler()
        query.args.forEachIndexed { i, arg ->
            typeHandler.setParameter(ps, i + 1, arg)
        }
        val hasResult = ps.execute()
        if (!hasResult) {
            throw RuntimeException("it does not have result")
        }
        return ps.resultSet
    }
}
