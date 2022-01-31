package dev.todaka.kbatis.resultmapper

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ResultMapperTest {
    @Test
    fun test() {
        val builder = MetaClassBuilderFactory.build(ClassWithNoArgConstructor::class.java)
        val actual = builder.build(mapOf("a" to 3, "b" to "abc"))
        assertThat(actual).isEqualTo(ClassWithNoArgConstructor(3, "abc"))
    }

    data class ClassWithNoArgConstructor(
        var a: Int = 0,
        var b: String = "",
    )
}
