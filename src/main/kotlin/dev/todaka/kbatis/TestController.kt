package dev.todaka.kbatis

import dev.todaka.kbatis.core.MapperFactory
import dev.todaka.kbatis.test.TestMapper
import dev.todaka.kbatis.test.User
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class TestController {
    @GetMapping("/test")
    fun test(): String {
        val mapper = MapperFactory().build(TestMapper::class.java)
        mapper.insert(User(1, "hoge"))
        return "OK"
    }
}
