package dev.todaka.kbatis.core

import dev.todaka.kbatis.driver.ConnOpenHelper
import dev.todaka.kbatis.driver.QueryExecutor
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class QueryExecutionTest {
    @Test
    fun test() {
        ConnOpenHelper.open().use { conn ->
            val kQuery = KStatement.build("select ? as first, ? as second", listOf(3, "a"))
            val expected = KResultSet(listOf("first", "second"), listOf(listOf(3, "a")))
            val actual = QueryExecutor.execute(conn, kQuery)
            assertThat(actual).isEqualTo(expected)
        }
    }
}
