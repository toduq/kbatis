package dev.todaka.kbatis.api

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class KSelect

/**
 * Insert method annotation for Mapper.
 *
 * ```
 * @KInsert("""
 *     insert into user (id, name)
 *     values (#{id}, #{name})
 * """)
 * fun insert(user: User)
 *
 * data class User(
 *     val id: Int,
 *     val name: String,
 * )
 * ```
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class KInsert(
    val value: String
)
