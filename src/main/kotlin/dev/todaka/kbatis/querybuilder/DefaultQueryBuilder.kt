package dev.todaka.kbatis.querybuilder

import dev.todaka.kbatis.core.KBatisRuntimeException
import dev.todaka.kbatis.core.ProxyArg
import dev.todaka.kbatis.core.QueryBuilder
import dev.todaka.kbatis.core.ResolvedQuery
import dev.todaka.kbatis.resultmapper.PropertyNameUtil
import java.lang.reflect.Modifier

/**
 * This class instantiate KStatement from parameters by parsing sql template.
 */
class DefaultQueryBuilder : QueryBuilder {
    private val argRegex = """#\{([A-Za-z0-9_]+)}""".toRegex()

    override fun build(arg: ProxyArg): ResolvedQuery {
        val typedArgs = mutableListOf<ResolvedQuery.TypedArg>()
        val replacedQuery = arg.template.replace(argRegex) { matchResult ->
            val label = matchResult.groupValues[1]
            val resolved = resolveArg(label, arg.namedTypedArgs)
                ?: throw KBatisRuntimeException("no matched fields for $label")
            typedArgs.add(resolved)
            "?"
        }
        return ResolvedQuery(replacedQuery, typedArgs)
    }

    private fun resolveArg(label: String, namedArgs: List<ProxyArg.NamedTypedArg>): ResolvedQuery.TypedArg? {
        // 1. resolve by method arg name
        namedArgs.forEach { arg ->
            if (arg.paramName == label) {
                return ResolvedQuery.TypedArg(type = arg.type, value = arg.value)
            }
        }
        // 2. resolve by method arg getter name
        namedArgs.forEach { arg ->
            val argClass = arg.type as? Class<*>
                ?: throw KBatisRuntimeException("unknown arg type found : ${arg.type}")
            val publicMethods = argClass.methods.filter { Modifier.isPublic(it.modifiers) }
            val getters = publicMethods
                .filter { it.parameters.isEmpty() && PropertyNameUtil.isGetter(it.name) }
                .associateBy { PropertyNameUtil.fieldNameOfGetter(it.name) }
            val getter = getters[label]
                ?: return@forEach
            return ResolvedQuery.TypedArg(type = getter.returnType, value = getter.invoke(arg.value))
        }
        return null
    }
}
