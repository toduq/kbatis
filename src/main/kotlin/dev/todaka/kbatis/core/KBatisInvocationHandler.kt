package dev.todaka.kbatis.core

import dev.todaka.kbatis.api.KInsert
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method

class KBatisInvocationHandler(
    private val option: ProxyFactoryOption
) : InvocationHandler {
    override fun invoke(proxy: Any, method: Method, args: Array<out Any>?): Any? {
        if (method.isAnnotationPresent(KInsert::class.java)) {
            val argMap = method.parameters.mapIndexed { i, param ->
                val name = if (param.isNamePresent) param.name else "arg$i"
                name!! to args?.get(i)
            }.toMap()
            val annotation = method.getAnnotation(KInsert::class.java)
            val proxyArg = ProxyArg(annotation.value, argMap)
            val resolvedQuery = option.queryBuilder.build(proxyArg)
            val unmappedResult = option.queryExecutor.exec(resolvedQuery)
            println("${method.name} called. SQL is $proxyArg. Result is $unmappedResult")
            return option.resultMapper.map(method.returnType, unmappedResult)
        } else {
            println("${method.name} called.")
            return null
        }
    }
}
