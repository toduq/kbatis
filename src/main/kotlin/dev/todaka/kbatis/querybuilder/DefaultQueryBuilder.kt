package dev.todaka.kbatis.querybuilder

import dev.todaka.kbatis.core.ProxyArg
import dev.todaka.kbatis.core.QueryBuilder
import dev.todaka.kbatis.core.ResolvedQuery

/**
 * This class instantiate KStatement from parameters by parsing sql template.
 */
class DefaultQueryBuilder : QueryBuilder {
    private val argRegex = """#\{([A-Za-z0-9_]+)}""".toRegex()

    override fun build(arg: ProxyArg): ResolvedQuery {
        val fieldNameToArg = mutableMapOf<String, Any>()
        arg.argMap.forEach { (_, arg) ->
            arg?.javaClass?.declaredFields?.forEach {
                it.isAccessible = true
                fieldNameToArg[it.name] = it.get(arg)
            }
        }
        val sqlArgsList = mutableListOf<Any>()
        argRegex.findAll(arg.template).forEach {
            val fieldName = it.groupValues[1]
            val sqlArg = fieldNameToArg[fieldName]
                ?: throw IllegalArgumentException("no field found for `$fieldName` in `$fieldNameToArg`")
            sqlArgsList.add(sqlArg)
        }
        val replacedQuery = arg.template.replace(argRegex, "?")
        return ResolvedQuery(replacedQuery, sqlArgsList)
    }
}
