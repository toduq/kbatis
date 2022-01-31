package dev.todaka.kbatis.querybuilder

import dev.todaka.kbatis.core.KStatement

/**
 * This class instantiate KStatement from parameters by parsing sql template.
 */
class QueryBuilder {
    private val argRegex = """#\{([A-Za-z0-9_]+)}""".toRegex()

    fun build(sql: String, args: Array<out Any>?): KStatement {
        val methodArg = args!![0]
        val methodArgMap = methodArg.javaClass.declaredFields.associate {
            it.isAccessible = true
            it.name to KStatement.Arg(it.get(methodArg), it.type)
        }
        val sqlArgsList = mutableListOf<KStatement.Arg>()
        argRegex.findAll(sql).forEach {
            val fieldName = it.groupValues[1]
            val sqlArg = methodArgMap[fieldName]
                ?: throw IllegalArgumentException("no field found for `$fieldName` in `$methodArgMap`")
            sqlArgsList.add(sqlArg)
        }
        val replacedQuery = sql.replace(argRegex, "?")
        return KStatement(replacedQuery, sqlArgsList)
    }
}
