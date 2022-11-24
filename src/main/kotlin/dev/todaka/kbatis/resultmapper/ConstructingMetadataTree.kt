package dev.todaka.kbatis.resultmapper

import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.deser.BeanDeserializer
import com.fasterxml.jackson.databind.deser.DefaultDeserializationContext
import com.fasterxml.jackson.databind.deser.std.CollectionDeserializer
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer
import com.fasterxml.jackson.module.kotlin.registerKotlinModule

data class ConstructingMetadataTree(
    val primaryFields: List<String> = listOf(),
    val simpleFields: List<String> = listOf(),
    val nestedFields: Map<String, ConstructingMetadataTree> = mapOf(),
    val nestedListFields: Map<String, ConstructingMetadataTree> = mapOf(),
)

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class PrimaryKey(
    vararg val value: String,
)

object ConstructingMetadataTreeFactory {
    fun <T> build(clazz: Class<T>): ConstructingMetadataTree {
        val type = mapper.typeFactory.constructType(clazz)
        val deser = (mapper.deserializationContext as DefaultDeserializationContext)
            .createInstance(mapper.deserializationConfig, mapper.createParser(""), mapper.injectableValues)
            .findRootValueDeserializer(type)
        return traverse(deser)
    }

    private fun traverse(deser: JsonDeserializer<Any>): ConstructingMetadataTree {
        return when (deser) {
            is CollectionDeserializer -> {
                traverse(deser.contentDeserializer)
            }
            is BeanDeserializer -> {
                val simpleFields = mutableListOf<String>()
                val nestedFields = mutableMapOf<String, ConstructingMetadataTree>()
                val nestedListFields = mutableMapOf<String, ConstructingMetadataTree>()

                deser.properties().forEach {
                    when (val fieldDeserializer = it.valueDeserializer) {
                        is StdScalarDeserializer<*> -> {
                            simpleFields.add(it.name)
                        }

                        is BeanDeserializer -> {
                            nestedFields[it.name] = traverse(fieldDeserializer)
                        }

                        is CollectionDeserializer -> {
                            nestedListFields[it.name] = traverse(fieldDeserializer.contentDeserializer)
                        }

                        else -> error("Unknown valueDeserializer ${it.valueDeserializer}")
                    }
                }

                // annotation or first field of class
                val primaryKeys = findPrimaryKeys(deser) ?: listOf(deser.properties().next().name)
                if (!simpleFields.containsAll(primaryKeys)) {
                    error("Primary key $primaryKeys is not found or not simple field.")
                }

                ConstructingMetadataTree(
                    primaryFields = primaryKeys,
                    simpleFields = simpleFields.filterNot { primaryKeys.contains(it) },
                    nestedFields = nestedFields,
                    nestedListFields = nestedListFields,
                )
            }

            else -> error("Unknown JsonDeserializer $deser")
        }
    }

    private fun findPrimaryKeys(deser: BeanDeserializer): List<String>? {
        val annotation = deser.valueType.rawClass.annotations.find { it is PrimaryKey } as PrimaryKey?
        return annotation?.value?.toList()
    }

    private val mapper = ObjectMapper().registerKotlinModule()
}
