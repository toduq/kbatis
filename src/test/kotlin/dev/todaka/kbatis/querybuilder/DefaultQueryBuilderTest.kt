package dev.todaka.kbatis.querybuilder

import dev.todaka.kbatis.core.ProxyArg
import dev.todaka.kbatis.core.ResolvedQuery
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class DefaultQueryBuilderTest {
    @Test
    fun test() {
        val builder = DefaultQueryBuilder()
        val arg = ProxyArg(
            template = """
                insert into user (id, name)
                values (#{id}, #{name})
            """.trimIndent(),
            namedTypedArgs = listOf(
                ProxyArg.NamedTypedArg(
                    type = User::class.java,
                    paramName = "_",
                    value = User(id = 3, name = "someone"),
                )
            )
        )
        val expected = ResolvedQuery(
            sql = """
                insert into user (id, name)
                values (?, ?)
            """.trimIndent(),
            typedArgs = listOf(
                ResolvedQuery.TypedArg(Int::class.java, 3),
                ResolvedQuery.TypedArg(String::class.java, "someone"),
            )
        )
        assertThat(builder.build(arg)).isEqualTo(expected)
    }

    private data class User(
        val id: Int,
        val name: String,
    )
}
