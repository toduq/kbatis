package dev.todaka.kbatis.core

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.lang.reflect.Type

class KQueryBuilderTest {
    @Test
    fun testMapperTest() {
        val query = """
            insert into user (id, name)
            values (#{id}, #{name})
        """.trimIndent()
        val arg = TestUser(3, "hoge")
        val result = KStatementBuilder().build(query, arrayOf(arg))

        val args = listOf(
            KStatement.Arg(3, Int::class.java as Type),
            KStatement.Arg("hoge", String::class.java as Type),
        )
        val sql = """
            insert into user (id, name)
            values (?, ?)
        """.trimIndent()
        assertThat(result).isEqualTo(KStatement(sql, args))
    }

    data class TestUser(
        val id: Int,
        val name: String,
    )
}
