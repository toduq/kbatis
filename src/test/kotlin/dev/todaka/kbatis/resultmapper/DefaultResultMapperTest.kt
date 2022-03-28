package dev.todaka.kbatis.resultmapper

import dev.todaka.kbatis.core.UnmappedResult
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class DefaultResultMapperTest {
    @Test
    fun testWithSetter() {
        val mapper = DefaultResultMapper()
        val unmappedResult = UnmappedResult(
            labels = listOf("theOther", "some"),
            rows = listOf(listOf("abc", 3), listOf("def", 4))
        )
        val expected = listOf(
            WithSetter(3, "abc"),
            WithSetter(4, "def"),
        )
        assertThat(mapper.map(WithSetter::class.java, unmappedResult)).isEqualTo(expected)
    }

    @Test
    fun testWithConstructor() {
        val mapper = DefaultResultMapper()
        val unmappedResult = UnmappedResult(
            labels = listOf("theOther", "some"),
            rows = listOf(listOf("abc", 3), listOf("def", 4))
        )
        val expected = listOf(
            WithConstructor(3, "abc"),
            WithConstructor(4, "def"),
        )
        assertThat(mapper.map(WithConstructor::class.java, unmappedResult)).isEqualTo(expected)
    }

    data class WithSetter(
        var some: Int = 0,
        var theOther: String = "",
    )

    data class WithConstructor(
        val some: Int,
        val theOther: String,
    )
}
