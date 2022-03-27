package dev.todaka.kbatis.resultmapper

import dev.todaka.kbatis.core.UnmappedResult
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class DefaultResultMapperTest {
    @Test
    fun test() {
        val mapper = DefaultResultMapper()
        val unmappedResult = UnmappedResult(
            labels = listOf("b", "a"),
            rows = listOf(listOf("abc", 3), listOf("def", 4))
        )
        val expected = listOf(
            ClassWithNoArgConstructor(3, "abc"),
            ClassWithNoArgConstructor(4, "def"),
        )
        assertThat(mapper.map(ClassWithNoArgConstructor::class.java, unmappedResult)).isEqualTo(expected)
    }

    data class ClassWithNoArgConstructor(
        var a: Int = 0,
        var b: String = "",
    )
}
