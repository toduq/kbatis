package dev.todaka.kbatis.core

import java.lang.reflect.Type
import java.sql.Connection

interface ProxyFactory {
    fun <T> build(clazz: Class<T>, conn: Connection): T
    fun <T> build(clazz: Class<T>, conn: Connection, option: ProxyFactoryOption): T
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
    val namedTypedArgs: List<NamedTypedArg>,
) {
    data class NamedTypedArg(
        val type: Type,
        val paramName: String,
        val value: Any?
    )
}

data class ResolvedQuery(
    val sql: String,
    val typedArgs: List<TypedArg>,
) {
    data class TypedArg(
        val type: Type,
        val value: Any?
    )
}

data class UnmappedResult(
    val labels: List<String>,
    val rows: List<List<Any?>>,
)
