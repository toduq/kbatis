package dev.todaka.kbatis.resultmapper

import dev.todaka.kbatis.core.KBatisInitializationException
import dev.todaka.kbatis.core.ResultMapper
import dev.todaka.kbatis.core.UnmappedResult
import java.lang.reflect.Constructor
import java.lang.reflect.Method
import java.lang.reflect.Modifier

class DefaultResultMapper : ResultMapper {
    override fun <T> map(clazz: Class<T>, unmapped: UnmappedResult): List<T> {
        val builder = MetaClassBuilderFactory.build(clazz)
        return unmapped.rows.map { row ->
            builder.build(row.mapIndexed { i, field -> unmapped.labels[i] to field }.toMap())
        }
    }
}

object MetaClassBuilderFactory {
    fun <T> build(clazz: Class<T>): MetaClassBuilder<T> {
        val publicConstructors = clazz.constructors.filter { Modifier.isPublic(it.modifiers) }
        val publicMethods = clazz.methods.filter { Modifier.isPublic(it.modifiers) }
        if (publicConstructors.isEmpty()) {
            throw ResultMapperInitializeException("${clazz.name} does not have any public constructors")
        }

        @Suppress("UNCHECKED_CAST")
        val noArgConstructor = publicConstructors.firstOrNull { it.parameters.isEmpty() } as Constructor<T>?

        if (noArgConstructor != null) {
            val setters = publicMethods
                .filter { it.parameters.size == 1 && PropertyNameUtil.isSetter(it.name) }
                .associateBy { PropertyNameUtil.fieldNameOfSetter(it.name) }
            return SetterBuilder(clazz, noArgConstructor, setters)
        }

        throw ResultMapperInitializeException("no way to construct ${clazz.name}")
    }
}

interface MetaClassBuilder<T> {
    fun build(fields: Map<String, Any?>): T
}

class SetterBuilder<T>(
    private val clazz: Class<T>,
    private val noArgConstructor: Constructor<T>,
    private val setters: Map<String, Method>
) : MetaClassBuilder<T> {
    override fun build(fields: Map<String, Any?>): T {
        val instance = noArgConstructor.newInstance()
        setters.forEach { (name, method) ->
            val value = fields[name]
                ?: throw NoColumnForSetterException("$name column not found for ${clazz.name}")
            method.invoke(instance, value)
        }
        return instance
    }
}

object PropertyNameUtil {
    fun isSetter(name: String): Boolean = name.startsWith("set") && name.length > 3
    fun fieldNameOfSetter(name: String): String = name[3].lowercase() + name.substring(4)
}

class ResultMapperInitializeException(msg: String) : KBatisInitializationException(msg)

class NoColumnForSetterException(msg: String) : KBatisInitializationException(msg)
