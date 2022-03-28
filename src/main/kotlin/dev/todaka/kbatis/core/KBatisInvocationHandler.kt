package dev.todaka.kbatis.core

import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.ParameterizedType

class KBatisInvocationHandler(
    private val option: ProxyFactoryOption
) : InvocationHandler {
    override fun invoke(proxy: Any, method: Method, args: Array<out Any>?): Any? {
        val annotations = listOfNotNull(
            method.getAnnotation(KSelect::class.java)?.let { it to it.value },
            method.getAnnotation(KInsert::class.java)?.let { it to it.value },
        )
        if (annotations.isEmpty()) {
            throw KBatisRuntimeException("no annotation found : $method")
        } else if (annotations.size >= 2) {
            throw KBatisRuntimeException("too many annotations found : $method")
        }
        val template = annotations[0].second
        val typedArgs = method.parameters.mapIndexed { i, param ->
            val name = if (param.isNamePresent) param.name else "arg$i"
            ProxyArg.NamedTypedArg(
                type = param.type,
                paramName = name,
                value = args?.get(i),
            )
        }
        val proxyArg = ProxyArg(template, typedArgs)

        val resolvedQuery = option.queryBuilder.build(proxyArg)
        val unmappedResult = option.queryExecutor.exec(resolvedQuery)

        return when (method.returnType) {
            Void.TYPE -> {
                // no return value required
                null
            }
            List::class.java -> {
                // list return type
                val mapTargetClass = method.genericReturnType
                    .let { it as? ParameterizedType }
                    ?.actualTypeArguments?.get(0)
                    ?.let { it as? Class<*> }
                    ?: throw KBatisRuntimeException("failed to cast return type into class reference : $method")
                option.resultMapper.map(mapTargetClass, unmappedResult)
            }
            else -> {
                // non-list return type
                val results = option.resultMapper.map(method.returnType, unmappedResult)
                if (results.size >= 2) {
                    throw KBatisRuntimeException("too many results for non list return type found : $method")
                }
                results[0]
            }
        }
    }
}
