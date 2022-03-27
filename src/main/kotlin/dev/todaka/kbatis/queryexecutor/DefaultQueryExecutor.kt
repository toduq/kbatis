package dev.todaka.kbatis.queryexecutor

import dev.todaka.kbatis.core.QueryExecutor
import dev.todaka.kbatis.core.ResolvedQuery
import dev.todaka.kbatis.core.UnmappedResult
import java.sql.Connection

class DefaultQueryExecutor(
    private val conn: Connection,
) : QueryExecutor {
    override fun exec(query: ResolvedQuery): UnmappedResult {
        conn.prepareStatement(query.sql).use { ps ->
            query.argList.forEachIndexed { i, arg ->
                ps.setObject(i + 1, arg)
            }
            val hasResult = ps.execute()
            if (!hasResult) {
                throw RuntimeException("it does not have result")
            }
            ps.resultSet.use { rs ->
                val meta = rs.metaData
                val columns = meta.columnCount
                val labels = (1..(columns)).map { meta.getColumnLabel(it).lowercase() }
                val values = mutableListOf<List<Any>>()
                while (rs.next()) {
                    values.add((1..columns).map { rs.getObject(it) })
                }
                return UnmappedResult(labels, values)
            }
        }
    }

//    fun execute(conn: Connection, query: KStatement): KResultSet {
//        conn.prepareStatement(query.sql).use { ps ->
//            val typeHandler = ParameterSerdeHandler()
//            query.args.forEachIndexed { i, arg ->
//                typeHandler.setParameter(ps, i + 1, arg)
//            }
//            val hasResult = ps.execute()
//            if (!hasResult) {
//                throw RuntimeException("it does not have result")
//            }
//            ps.resultSet.use { rs ->
//                val meta = rs.metaData
//                val columns = meta.columnCount
//                val labels = (1..(columns)).map { meta.getColumnLabel(it).lowercase() }
//                val values = mutableListOf<List<Any>>()
//                while (rs.next()) {
//                    values.add((1..columns).map { rs.getObject(it) })
//                }
//                return KResultSet(labels, values)
//            }
//        }
//    }

}
