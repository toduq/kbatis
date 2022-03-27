package dev.todaka.kbatis.queryexecutor

import java.lang.reflect.Type
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Types

/**
 * Type conversion between Object -> PreparedStatement, ResultSet -> Object.
 */
class ParameterSerdeHandler {

    private val serializeMap = mutableMapOf<Type, ColumnSerde<*>>()
    private val deserializeMap = mutableMapOf<Int, ColumnSerde<*>>()

    init {
        serializeMap[Int::class.java] = IntColumnSerde()
        serializeMap[Integer::class.java] = IntColumnSerde()
        deserializeMap[Types.INTEGER] = IntColumnSerde()

        serializeMap[String::class.java] = StringColumnSerde()
        deserializeMap[Types.VARCHAR] = StringColumnSerde()
    }

//    fun setParameter(ps: PreparedStatement, index: Int, arg: KStatement.Arg) {
//        getSerializer(arg.type).serialize(ps, index, arg.value)
//    }

    @Suppress("UNCHECKED_CAST")
    private fun getSerializer(type: Type): ColumnSerde<Any> {
        return serializeMap[type] as ColumnSerde<Any>?
            ?: throw IllegalArgumentException("no serializer found for type $type")
    }
}

/**
 * https://github.com/mybatis/mybatis-3/blob/395be63314d77cf5b956a77631ab0620de26df7a/src/main/java/org/apache/ibatis/type/LongTypeHandler.java
 */
interface ColumnSerde<T> {
    fun serialize(ps: PreparedStatement, index: Int, arg: T)
    fun deserialize(rs: ResultSet, index: Int): T?
}

class IntColumnSerde : ColumnSerde<Int> {
    override fun serialize(ps: PreparedStatement, index: Int, arg: Int) {
        ps.setInt(index, arg)
    }

    override fun deserialize(rs: ResultSet, index: Int): Int? {
        val result = rs.getInt(index)
        return if (result == 0 && rs.wasNull()) null else result
    }
}

class StringColumnSerde : ColumnSerde<String> {
    override fun serialize(ps: PreparedStatement, index: Int, arg: String) {
        ps.setString(index, arg)
    }

    override fun deserialize(rs: ResultSet, index: Int): String? {
        return rs.getString(index)
    }
}
