package dev.todaka.kbatis.test

import dev.todaka.kbatis.core.KInsert

interface TestMapper {
    @KInsert("""
        insert into user (id, name)
        values (#{id}, #{name})
    """)
    fun insert(user: User)
}

data class User(
    val id: Int,
    val name: String,
)
