package dev.todaka.kbatis.core

import java.lang.reflect.Proxy

/**
 * MapperFactory receive interface classes and generates Mapper instance.
 */
class MapperFactory {
    @Suppress("UNCHECKED_CAST")
    fun <T> build(klass: Class<T>): T {
        return Proxy.newProxyInstance(
            this::class.java.classLoader,
            arrayOf<Class<*>>(klass),
            KBatisInvocationHandler()
        ) as T
    }
}
