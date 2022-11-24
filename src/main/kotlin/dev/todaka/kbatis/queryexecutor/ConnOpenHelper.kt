package dev.todaka.kbatis.queryexecutor

import java.sql.Connection
import java.sql.DriverManager

object ConnOpenHelper {
    fun open(suffix: String = System.currentTimeMillis().toString()): Connection {
        Class.forName("org.h2.Driver")
        return DriverManager.getConnection("jdbc:h2:/tmp/h2_db_$suffix", "", "")
    }
}
