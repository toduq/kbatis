package dev.todaka.kbatis.core

data class KResultSet(
    val labels: List<String>,
    val rows: List<List<Any>>
)
