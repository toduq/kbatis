package dev.todaka.kbatis.queryexecutor

import dev.todaka.kbatis.core.KBatisRuntimeException
import dev.todaka.kbatis.core.QueryExecutor
import dev.todaka.kbatis.core.ResolvedQuery
import dev.todaka.kbatis.core.UnmappedResult
import java.sql.Connection
import java.sql.Types

class DefaultQueryExecutor(
    private val conn: Connection,
) : QueryExecutor {

    // https://dev.mysql.com/doc/connector-j/8.0/en/connector-j-reference-type-conversions.html
    private val typeToSqlType = mapOf(
        Int::class.java to Types.INTEGER,
        Long::class.java to Types.BIGINT,
        Double::class.java to Types.DOUBLE,
        String::class.java to Types.VARCHAR,
        Boolean::class.java to Types.BOOLEAN,
    )

    override fun exec(query: ResolvedQuery): UnmappedResult {
        conn.prepareStatement(query.sql).use { ps ->
            query.typedArgs.forEachIndexed { i, arg ->
                val sqlType = typeToSqlType[arg.type]
                    ?: throw KBatisRuntimeException("no java.sql.Types mapping found for ${arg.type}")
                ps.setObject(i + 1, arg.value, sqlType)
            }
            val hasResult = ps.execute()
            if (!hasResult) {
                return UnmappedResult(listOf(), listOf())
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
}
