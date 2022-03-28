# Kbatis

An SQL Mapper framework for JVM languages, especially for Kotlin. It provides a simple way to execute SQL and to fetch
data into java

## Code Sample

```kotlin
interface UserMapper {
    @KInsert("insert into users #{user}")
    fun insert(user: InsertUser): Int // returns inserted id

    @KSelect(
        """
        select * from users
        left join posts using (user_id)
        where user_id = #{userId}
    """
    )
    fun selectById(userId: Long): SelectedUser?
}

data class InsertUser(
    val name: String,
    val email: String,
)

data class SelectedUser(
    @PrimaryKey
    val userId: Long,
    val name: String,
    val email: String,
    val posts: List<Post>
) {
    data class Post(
        @PrimaryKey
        val postId: Long,
        val content: String,
    )
}
```

## Internals

```
+---------------+    +----------------+    +---------------+
| Query Builder | -> | Query Executor | -> | Result Mapper |
+---------------+    +----------------+    +---------------+
```

### Query Builder

The Query Builder builds SQL from annotations and method parameters. It uses mustache template for building complex
query.

### Query Executor

Only the Query Executor has a JDBC dependency. It converts Java types into JDBC types, and execute SQL, and fetch
results into Java types.

### Result Mapper

A result of the Query Executor is a list of columns. The Result Mapper maps the result into nested objects like Jackson.
