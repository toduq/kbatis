package dev.todaka.kbatis

import dev.todaka.kbatis.core.DefaultProxyFactory
import dev.todaka.kbatis.core.KInsert
import dev.todaka.kbatis.core.KSelect
import dev.todaka.kbatis.queryexecutor.ConnOpenHelper
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
class TestController {
    @GetMapping("/test")
    fun test(): Any {
        ConnOpenHelper.open().use { conn ->
            val mapper = DefaultProxyFactory().build(TestMapper::class.java, conn)
            mapper.createTable()
            mapper.insert(UUID.randomUUID().toString())
            return mapper.selectAll()
        }
    }

    interface TestMapper {
        @KInsert(
            """
            create table if not exists test_user (
                id int not null primary key auto_increment,
                name varchar(255) not null
            )
            """
        )
        fun createTable()

        @KInsert(
            """
            insert into test_user (name)
            values (#{name})
            """
        )
        fun insert(name: String)

        @KSelect(
            """
            select * from test_user
            """
        )
        fun selectAll(): List<User>
    }

    data class User(
        var id: Int,
        var name: String,
    )
}
