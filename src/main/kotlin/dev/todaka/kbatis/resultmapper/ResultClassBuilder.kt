package dev.todaka.kbatis.resultmapper

import dev.todaka.kbatis.core.KBatisInitializationException
import dev.todaka.kbatis.core.KBatisRuntimeException
import java.lang.reflect.Constructor
import java.lang.reflect.Method
import java.lang.reflect.Modifier

interface ResultClassBuilder<T> {
    fun build(fields: Map<String, Any?>): T
}

object ResultClassBuilderFactory {
    fun <T> build(clazz: Class<T>): ResultClassBuilder<T> {
        val publicConstructors = clazz.constructors.filter { Modifier.isPublic(it.modifiers) }
        val publicMethods = clazz.methods.filter { Modifier.isPublic(it.modifiers) }
        if (publicConstructors.isEmpty()) {
            throw KBatisInitializationException("${clazz.name} does not have any public constructors")
        }

        @Suppress("UNCHECKED_CAST")
        val noArgConstructor = publicConstructors.firstOrNull { it.parameters.isEmpty() } as Constructor<T>?
        if (noArgConstructor != null) {
            val setters = publicMethods
                .filter { it.parameters.size == 1 && PropertyNameUtil.isSetter(it.name) }
                .associateBy { PropertyNameUtil.fieldNameOfSetter(it.name) }
            if (setters.isNotEmpty()) {
                return SetterBuilder(clazz, noArgConstructor, setters)
            }
        }

        @Suppress("UNCHECKED_CAST")
        val argsConstructor = publicConstructors.firstOrNull { it.parameters.isNotEmpty() } as Constructor<T>?
        if (argsConstructor != null) {
            if (argsConstructor.parameters.any { !it.isNamePresent }) {
                throw KBatisInitializationException("constructor does not retain its parameter name : $clazz")
            }
            val argNameList = argsConstructor.parameters.map { it.name }
            return ConstructorBuilder(clazz, argsConstructor, argNameList)
        }

        // TODO: probably not reaches here.
        throw KBatisInitializationException("no way to construct ${clazz.name}")
    }
}

class SetterBuilder<T>(
    private val clazz: Class<T>,
    private val noArgConstructor: Constructor<T>,
    private val setters: Map<String, Method>
) : ResultClassBuilder<T> {
    override fun build(fields: Map<String, Any?>): T {
        val instance = noArgConstructor.newInstance()
        setters.forEach { (name, method) ->
            val value = fields[name]
                ?: throw KBatisRuntimeException("$name column not found for ${clazz.name}")
            method.invoke(instance, value)
        }
        return instance
    }
}

class ConstructorBuilder<T>(
    private val clazz: Class<T>,
    private val argsConstructor: Constructor<T>,
    private val argNameList: List<String>,
) : ResultClassBuilder<T> {
    override fun build(fields: Map<String, Any?>): T {
        val args = argNameList.map { name ->
            fields[name]
                ?: throw KBatisRuntimeException("$name column not found for ${clazz.name}")
        }.toTypedArray()
        return argsConstructor.newInstance(*args)
    }
}

object PropertyNameUtil {
    fun isSetter(name: String): Boolean = name.startsWith("set") && name.length > 3
    fun isGetter(name: String): Boolean = name.startsWith("get") && name.length > 3
    fun fieldNameOfSetter(name: String): String = name[3].lowercase() + name.substring(4)
    fun fieldNameOfGetter(name: String): String = name[3].lowercase() + name.substring(4)
}
