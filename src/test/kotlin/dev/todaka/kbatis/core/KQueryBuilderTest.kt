package dev.todaka.kbatis.core

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class KQueryBuilderTest {
    @Test
    fun testMapperTest() {
        val query = """
            insert into user (id, name)
            values (#{id}, #{name})
        """.trimIndent()
        val arg = TestUser(3, "hoge")
        val result = KQueryBuilder().build(query, arrayOf(arg))

        assertThat(result).isEqualTo(KQuery("""
            insert into user (id, name)
            values (?, ?)
        """.trimIndent(), listOf("3", "hoge")))
    }

    data class TestUser(
        val id: Int,
        val name: String,
    )
}
