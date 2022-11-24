package dev.todaka.kbatis.resultmapper

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ConstructingMetadataTreeTest {
    @Test
    fun testNested() {
        val expected = ConstructingMetadataTree(
            listOf("a"), listOf("b"),
            mapOf("nested" to ConstructingMetadataTree(listOf("c"))),
            mapOf("nestedList" to ConstructingMetadataTree(listOf("c"))),
        )
        assertThat(ConstructingMetadataTreeFactory.build(Parent::class.java)).isEqualTo(expected)
    }

    @Test
    fun testMultiplePrimary() {
        val expected = ConstructingMetadataTree(listOf("b", "c"), listOf("a"))
        assertThat(ConstructingMetadataTreeFactory.build(MultiplePrimary::class.java)).isEqualTo(expected)
    }

    data class Parent(
        val a: String,
        val b: String,
        val nested: Child,
        val nestedList: List<Child>,
    ) {
        data class Child(
            val c: String,
        )
    }

    @PrimaryKey("b", "c")
    data class MultiplePrimary(
        val a: Int,
        val b: String,
        val c: Double,
    )

}
