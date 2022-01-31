package dev.todaka.kbatis.core

/**
 * KBatis base exception class
 *
 * KBatisException
 *  +- KBatisInitializationException
 *  +- KBatisRuntimeException
 */
open class KBatisException(msg: String) : RuntimeException(msg)

open class KBatisInitializationException(msg: String) : KBatisException(msg)

open class KBatisRuntimeException(msg: String) : KBatisException(msg)
