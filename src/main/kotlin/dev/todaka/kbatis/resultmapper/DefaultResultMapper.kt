package dev.todaka.kbatis.resultmapper

import dev.todaka.kbatis.core.ResultMapper
import dev.todaka.kbatis.core.UnmappedResult

class DefaultResultMapper : ResultMapper {
    override fun <T> map(clazz: Class<T>, unmapped: UnmappedResult): List<T> {
        val tree = ConstructingMetadataTreeFactory.build(clazz)
        
    }
}
