package dev.todaka.kbatis

import dev.todaka.kbatis.api.KInsert
import dev.todaka.kbatis.core.MapperFactory
import dev.todaka.kbatis.queryexecutor.ConnOpenHelper
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class TestController {
    @GetMapping("/test")
    fun test(): String {
        val mapper = MapperFactory().build(TestMapper::class.java, ConnOpenHelper.open())
        mapper.insert(User(1, "hoge"))
        return "OK"
    }

    interface TestMapper {
        @KInsert(
            """
            insert into user (id, name)
            values (#{id}, #{name})
            """
        )
        fun insert(user: User)
    }

    data class User(
        val id: Int,
        val name: String,
    )
}
