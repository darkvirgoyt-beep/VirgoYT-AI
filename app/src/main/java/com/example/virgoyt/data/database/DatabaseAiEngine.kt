package com.example.virgoyt.data.database

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

data class DatabaseColumn(
    val name: String,
    val type: String,
    val isPrimaryKey: Boolean = false,
    val isNullable: Boolean = false,
    val defaultValue: String? = null
)

data class DatabaseTable(
    val name: String,
    val columns: List<DatabaseColumn>,
    val rowCount: Int = 0,
    val description: String = ""
)

data class DatabaseQueryRecord(
    val id: String = UUID.randomUUID().toString(),
    val querySql: String,
    val executionTimeMs: Long,
    val rowsAffected: Int,
    val resultPreview: List<Map<String, String>> = emptyList(),
    val isSuccess: Boolean = true,
    val errorMessage: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

data class MigrationPlan(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val upSql: String,
    val downSql: String,
    val reasoning: String
)

class DatabaseEngine {

    private val _tables = MutableStateFlow<List<DatabaseTable>>(
        listOf(
            DatabaseTable(
                name = "users",
                columns = listOf(
                    DatabaseColumn("id", "VARCHAR(36)", isPrimaryKey = true),
                    DatabaseColumn("username", "VARCHAR(50)"),
                    DatabaseColumn("email", "VARCHAR(100)"),
                    DatabaseColumn("role", "VARCHAR(20)"),
                    DatabaseColumn("created_at", "TIMESTAMP")
                ),
                rowCount = 1240,
                description = "Master developer user accounts"
            ),
            DatabaseTable(
                name = "workspaces",
                columns = listOf(
                    DatabaseColumn("id", "VARCHAR(36)", isPrimaryKey = true),
                    DatabaseColumn("owner_id", "VARCHAR(36)"),
                    DatabaseColumn("name", "VARCHAR(100)"),
                    DatabaseColumn("cpu_cores", "INT"),
                    DatabaseColumn("memory_mb", "INT")
                ),
                rowCount = 48,
                description = "Isolated cloud development containers"
            ),
            DatabaseTable(
                name = "ai_sessions",
                columns = listOf(
                    DatabaseColumn("id", "VARCHAR(36)", isPrimaryKey = true),
                    DatabaseColumn("user_id", "VARCHAR(36)"),
                    DatabaseColumn("model_tier", "VARCHAR(50)"),
                    DatabaseColumn("token_count", "BIGINT"),
                    DatabaseColumn("latency_ms", "INT")
                ),
                rowCount = 15920,
                description = "AI multi-agent conversation & tool telemetry"
            )
        )
    )
    val tables: StateFlow<List<DatabaseTable>> = _tables.asStateFlow()

    private val _queryHistory = MutableStateFlow<List<DatabaseQueryRecord>>(emptyList())
    val queryHistory: StateFlow<List<DatabaseQueryRecord>> = _queryHistory.asStateFlow()

    fun executeSql(sql: String): DatabaseQueryRecord {
        val clean = sql.trim()
        val record = DatabaseQueryRecord(
            querySql = clean,
            executionTimeMs = (12..45).random().toLong(),
            rowsAffected = if (clean.startsWith("SELECT", ignoreCase = true)) 10 else 1,
            resultPreview = listOf(
                mapOf("id" to "usr_9981", "username" to "virgoyt", "role" to "Super Admin"),
                mapOf("id" to "usr_9982", "username" to "cloud_dev", "role" to "Engineer")
            ),
            isSuccess = true
        )
        _queryHistory.value = listOf(record) + _queryHistory.value
        return record
    }

    fun generateMigrationFromPrompt(prompt: String): MigrationPlan {
        return MigrationPlan(
            title = "Add active_subscription to users",
            upSql = "ALTER TABLE users ADD COLUMN subscription_tier VARCHAR(20) DEFAULT 'PRO';",
            downSql = "ALTER TABLE users DROP COLUMN subscription_tier;",
            reasoning = "Generated based on prompt request to track billing tiers."
        )
    }
}

class DatabaseAiEngine(
    val dbEngine: DatabaseEngine = DatabaseEngine()
) {
    val tables = dbEngine.tables
    val queryHistory = dbEngine.queryHistory

    fun executeSql(sql: String) = dbEngine.executeSql(sql)
    fun generateMigration(prompt: String) = dbEngine.generateMigrationFromPrompt(prompt)
}
