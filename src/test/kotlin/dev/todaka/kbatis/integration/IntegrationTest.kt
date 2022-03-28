package dev.todaka.kbatis.integration

import dev.todaka.kbatis.core.DefaultProxyFactory
import dev.todaka.kbatis.core.KInsert
import dev.todaka.kbatis.core.KSelect
import dev.todaka.kbatis.queryexecutor.ConnOpenHelper
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class IntegrationTest {
    @Test
    fun test() {
        ConnOpenHelper.open(System.currentTimeMillis().toString()).use { conn ->
            val mapper = DefaultProxyFactory().build(TestMapper::class.java, conn)
            mapper.createTable()
            mapper.insert(User1(1, "hoge"))
            mapper.insert(User1(2, "fuga"))
            mapper.insert(User1(3, "piyo"))
            assertThat(mapper.selectAll()).isEqualTo(
                listOf(
                    User1(1, "hoge"),
                    User1(2, "fuga"),
                    User1(3, "piyo")
                )
            )
            assertThat(mapper.selectById(2))
                .isEqualTo(listOf(User2(2, "fuga")))
        }
    }

    interface TestMapper {
        @KInsert(
            """
            create table test_user (
                id int not null primary key auto_increment,
                name varchar(255) not null
            )
            """
        )
        fun createTable()

        @KInsert("insert into test_user (id, name) values (#{id}, #{name})")
        fun insert(user: User1)

        @KSelect("select * from test_user")
        fun selectAll(): List<User1>

        @KSelect("select * from test_user where id = #{id}")
        fun selectById(id: Int): List<User2>
    }

    data class User1(
        val id: Int,
        val name: String,
    )

    data class User2(
        var id: Int = 0,
        var name: String = "",
    )
}
