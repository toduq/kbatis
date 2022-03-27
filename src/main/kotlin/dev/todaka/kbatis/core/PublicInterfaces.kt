package dev.todaka.kbatis.core

interface ProxyFactory<T> {
    fun build(proxy: Class<T>, option: ProxyFactoryOption): T
}

interface QueryBuilder {
    fun build(arg: ProxyArg): ResolvedQuery
}

interface QueryExecutor {
    fun exec(query: ResolvedQuery): UnmappedResult
}

interface ResultMapper {
    fun <T> map(clazz: Class<T>, unmapped: UnmappedResult): List<T>
}

data class ProxyFactoryOption(
    val queryBuilder: QueryBuilder,
    val queryExecutor: QueryExecutor,
    val resultMapper: ResultMapper,
)

data class ProxyArg(
    val template: String,
    val argMap: Map<String, Any?>,
)

data class ResolvedQuery(
    val sql: String,
    val argList: List<Any?>,
)

data class UnmappedResult(
    val labels: List<String>,
    val rows: List<List<Any?>>,
)
