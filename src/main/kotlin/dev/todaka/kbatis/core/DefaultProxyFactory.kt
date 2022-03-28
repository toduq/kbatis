package dev.todaka.kbatis.core

import dev.todaka.kbatis.querybuilder.DefaultQueryBuilder
import dev.todaka.kbatis.queryexecutor.DefaultQueryExecutor
import dev.todaka.kbatis.resultmapper.DefaultResultMapper
import java.lang.reflect.Proxy
import java.sql.Connection

/**
 * MapperFactory receive interface classes and generates Mapper instance.
 */
class DefaultProxyFactory : ProxyFactory {
    override fun <T> build(
        clazz: Class<T>,
        conn: Connection,
    ): T {
        return build(
            clazz, conn, ProxyFactoryOption(
                queryBuilder = DefaultQueryBuilder(),
                queryExecutor = DefaultQueryExecutor(conn),
                resultMapper = DefaultResultMapper(),
            )
        )
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> build(
        clazz: Class<T>,
        conn: Connection,
        option: ProxyFactoryOption,
    ): T {
        return Proxy.newProxyInstance(
            this::class.java.classLoader,
            arrayOf<Class<*>>(clazz),
            KBatisInvocationHandler(option)
        ) as T
    }
}
