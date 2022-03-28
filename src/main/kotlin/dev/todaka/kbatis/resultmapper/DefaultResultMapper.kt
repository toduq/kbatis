package dev.todaka.kbatis.resultmapper

import dev.todaka.kbatis.core.ResultMapper
import dev.todaka.kbatis.core.UnmappedResult

class DefaultResultMapper : ResultMapper {
    override fun <T> map(clazz: Class<T>, unmapped: UnmappedResult): List<T> {
        val builder = ResultClassBuilderFactory.build(clazz)
        return unmapped.rows.map { row ->
            builder.build(row.mapIndexed { i, field -> unmapped.labels[i] to field }.toMap())
        }
    }
}
