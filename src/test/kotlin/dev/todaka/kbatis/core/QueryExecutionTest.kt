package dev.todaka.kbatis.core

import dev.todaka.kbatis.driver.ConnOpenHelper
import dev.todaka.kbatis.driver.QueryExecutor
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class QueryExecutionTest {
    @Test
    fun test() {
        ConnOpenHelper.open().use { conn ->
            val kQuery = KQuery("""
                select ?
            """.trimIndent(), listOf("3"))
            val result = QueryExecutor.execute(conn, kQuery).use { rs ->
                rs.next()
                rs.getString(1)
            }
            assertThat(result).isEqualTo("3")
        }
    }
}
