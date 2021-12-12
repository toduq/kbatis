package dev.todaka.kbatis.core

class KQueryBuilder {
    private val argRegex = """#\{([A-Za-z0-9_]+)}""".toRegex()

    fun build(sql: String, args: Array<out Any>?): KQuery {
        val methodArg = args!![0]
        val methodArgMap = methodArg.javaClass.declaredFields.associate {
            it.isAccessible = true
            it.name to it.get(methodArg)
        }
        val sqlArgsList = mutableListOf<String>()
        argRegex.findAll(sql).forEach {
            val fieldName = it.groupValues[1]
            val sqlArg = methodArgMap[fieldName]
                ?: throw IllegalArgumentException("no field found for `$fieldName` in `$methodArgMap`")
            sqlArgsList.add(sqlArg.toString())
        }
        val replacedQuery = sql.replace(argRegex, "?")
        return KQuery(replacedQuery, sqlArgsList)
    }
}
