package dev.todaka.kbatis.resultmapper

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import dev.todaka.kbatis.core.ResultMapper
import dev.todaka.kbatis.core.UnmappedResult
import java.util.*

class DefaultResultMapper : ResultMapper {
    override fun <T> map(clazz: Class<T>, unmapped: UnmappedResult): List<T> {
        val tree = ConstructingMetadataTreeFactory.build(clazz)
        check(requireLabels(tree, unmapped.labels))
        val structured = traverse(tree, unmapped)

        // TODO: Calling multiple convertValue is probably bad for performance.
        return structured.map { objectMapper.convertValue(it, clazz) }
    }

    private fun requireLabels(tree: ConstructingMetadataTree, labels: List<String>): Boolean {
        return labels.containsAll(traverseRequireLabels(tree))
    }

    private fun traverseRequireLabels(tree: ConstructingMetadataTree): Set<String> {
        val fields = tree.primaryFields + tree.simpleFields +
                tree.nestedFields.flatMap { traverseRequireLabels(it.value) } +
                tree.nestedListFields.flatMap { traverseRequireLabels(it.value) }
        return fields.toSet()
    }

    private fun traverse(tree: ConstructingMetadataTree, unmapped: UnmappedResult): List<Map<String, Any?>> {
        val primaryIndex = tree.primaryFields.toLabelMap(unmapped.labels)
        val mergedRows = TreeMap<Any, MutableList<List<Any?>>>()
        unmapped.rows.forEach { row ->
            mergedRows
                .computeIfAbsent(toMapKey(primaryIndex.map { row[it.value]!! })) { mutableListOf() }
                .add(row)
        }
        return mergedRows.map { (_, rows) -> traverseSingle(tree, UnmappedResult(unmapped.labels, rows)) }
    }

    private fun toMapKey(list: List<Any?>): Any {
        return when (list.size) {
            1 -> list[0]!!
            2 -> Pair(list[0], list[1])
            3 -> Triple(list[0], list[1], list[2])
            else -> error("bad list for primary key : ${list.size}")
        }
    }

    private fun traverseSingle(tree: ConstructingMetadataTree, unmapped: UnmappedResult): Map<String, Any?> {
        val firstRow = unmapped.rows[0]
        val primaryFields = tree.primaryFields.toLabelMap(unmapped.labels)
            .map { (label, ind) -> label to firstRow[ind] }.toMap()
        val simpleFields = tree.simpleFields.toLabelMap(unmapped.labels)
            .map { (label, ind) -> label to firstRow[ind] }.toMap()
        val nestedFields = tree.nestedFields.mapValues { (_, childTree) ->
            traverseSingle(childTree, UnmappedResult(unmapped.labels, unmapped.rows))
        }
        val nestedListFields = tree.nestedListFields.mapValues { (_, childTree) ->
            traverse(childTree, UnmappedResult(unmapped.labels, unmapped.rows))
        }
        return primaryFields + simpleFields + nestedFields + nestedListFields
    }

    companion object {
        private val objectMapper = ObjectMapper().registerKotlinModule()
    }
}

private fun <T> List<T>.toLabelMap(labels: List<T>): Map<T, Int> =
    associateWith { labels.indexOf(it) }

