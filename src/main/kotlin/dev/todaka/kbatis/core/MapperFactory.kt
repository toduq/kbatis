package dev.todaka.kbatis.core

import dev.todaka.kbatis.querybuilder.DefaultQueryBuilder
import dev.todaka.kbatis.queryexecutor.DefaultQueryExecutor
import dev.todaka.kbatis.resultmapper.DefaultResultMapper
import java.lang.reflect.Proxy
import java.sql.Connection

/**
 * MapperFactory receive interface classes and generates Mapper instance.
 */
class MapperFactory {
    @Suppress("UNCHECKED_CAST")
    fun <T> build(klass: Class<T>, conn: Connection): T {
        return Proxy.newProxyInstance(
            this::class.java.classLoader,
            arrayOf<Class<*>>(klass),
            KBatisInvocationHandler(
                ProxyFactoryOption(
                    queryBuilder = DefaultQueryBuilder(),
                    queryExecutor = DefaultQueryExecutor(conn),
                    resultMapper = DefaultResultMapper(),
                )
            )
        ) as T
    }
}
