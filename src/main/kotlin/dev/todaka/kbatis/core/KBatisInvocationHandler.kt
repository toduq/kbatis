package dev.todaka.kbatis.core

import dev.todaka.kbatis.api.KInsert
import dev.todaka.kbatis.querybuilder.QueryBuilder
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method

class KBatisInvocationHandler : InvocationHandler {
    override fun invoke(proxy: Any, method: Method, args: Array<out Any>?) {
        if (method.isAnnotationPresent(KInsert::class.java)) {
            val annotation = method.getAnnotation(KInsert::class.java)
            val kQuery = QueryBuilder().build(annotation.value, args)
            println("${method.name} called. SQL is $kQuery")
        } else {
            println("${method.name} called.")
        }
    }
}
