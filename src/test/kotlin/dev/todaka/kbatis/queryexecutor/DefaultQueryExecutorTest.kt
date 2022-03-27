package dev.todaka.kbatis.queryexecutor

import dev.todaka.kbatis.core.ResolvedQuery
import dev.todaka.kbatis.core.UnmappedResult
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class DefaultQueryExecutorTest {
    @Test
    fun test() {
        ConnOpenHelper.open().use { conn ->
            val executor = DefaultQueryExecutor(conn)
            val resolvedQuery = ResolvedQuery(
                sql = "select 1 + ? as num, upper(?) as str",
                argList = listOf(3, "some")
            )
            val expected = UnmappedResult(
                labels = listOf("num", "str"),
                rows = listOf(listOf(4, "SOME"))
            )
            assertThat(executor.exec(resolvedQuery)).isEqualTo(expected)
        }
    }
}
