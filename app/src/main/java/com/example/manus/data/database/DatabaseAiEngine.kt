package com.example.manus.data.database

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

enum class DatabaseEngine(val displayName: String, val type: String, val iconEmoji: String, val defaultPort: Int) {
    POSTGRESQL("PostgreSQL 16", "Relational SQL", "🐘", 5432),
    MYSQL("MySQL 8.4", "Relational SQL", "🐬", 3306),
    SQLITE("SQLite 3 (Embedded)", "Local Embedded SQL", "🪶", 0),
    MONGODB("MongoDB 7.0", "Document NoSQL", "🍃", 27017),
    REDIS("Redis 7.2", "In-Memory Key-Value & Cache", "⚡", 6379),
    SUPABASE("Supabase Cloud", "Postgres + Realtime + Auth", "⚡", 54321),
    FIREBASE("Firebase Firestore", "NoSQL Realtime Cloud", "🔥", 443)
}

data class DatabaseColumn(
    val name: String,
    val type: String,
    val isPrimaryKey: Boolean = false,
    val isNullable: Boolean = false,
    val isIndexed: Boolean = false,
    val defaultValue: String? = null
)

data class DatabaseTable(
    val tableName: String,
    val engine: DatabaseEngine = DatabaseEngine.POSTGRESQL,
    val rowCountEstimate: Long = 1250L,
    val sizeFormatted: String = "4.2 MB",
    val columns: List<DatabaseColumn>,
    val sqlCreateStatement: String
)

data class DatabaseQueryRecord(
    val id: String = UUID.randomUUID().toString(),
    val queryText: String,
    val executionTimeMs: Long,
    val rowsAffected: Int,
    val resultsJson: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class MigrationPlan(
    val version: String,
    val title: String,
    val upSql: String,
    val downSql: String,
    val status: String = "APPLIED" // APPLIED, PENDING
)

class DatabaseAiEngine {
    private val _selectedEngine = MutableStateFlow(DatabaseEngine.POSTGRESQL)
    val selectedEngine: StateFlow<DatabaseEngine> = _selectedEngine.asStateFlow()

    private val _tables = MutableStateFlow<List<DatabaseTable>>(
        listOf(
            DatabaseTable(
                tableName = "users",
                engine = DatabaseEngine.POSTGRESQL,
                rowCountEstimate = 14200L,
                sizeFormatted = "2.8 MB",
                columns = listOf(
                    DatabaseColumn("id", "UUID", isPrimaryKey = true, isIndexed = true),
                    DatabaseColumn("username", "VARCHAR(64)", isNullable = false, isIndexed = true),
                    DatabaseColumn("email", "VARCHAR(255)", isNullable = false, isIndexed = true),
                    DatabaseColumn("password_hash", "TEXT", isNullable = false),
                    DatabaseColumn("role", "VARCHAR(32)", defaultValue = "'developer'"),
                    DatabaseColumn("created_at", "TIMESTAMPTZ", defaultValue = "NOW()", isIndexed = true)
                ),
                sqlCreateStatement = """
                    CREATE TABLE users (
                        id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                        username VARCHAR(64) UNIQUE NOT NULL,
                        email VARCHAR(255) UNIQUE NOT NULL,
                        password_hash TEXT NOT NULL,
                        role VARCHAR(32) DEFAULT 'developer',
                        created_at TIMESTAMPTZ DEFAULT NOW()
                    );
                    CREATE INDEX idx_users_username ON users(username);
                    CREATE INDEX idx_users_email ON users(email);
                """.trimIndent()
            ),
            DatabaseTable(
                tableName = "projects",
                engine = DatabaseEngine.POSTGRESQL,
                rowCountEstimate = 890L,
                sizeFormatted = "1.1 MB",
                columns = listOf(
                    DatabaseColumn("id", "UUID", isPrimaryKey = true, isIndexed = true),
                    DatabaseColumn("user_id", "UUID", isNullable = false, isIndexed = true),
                    DatabaseColumn("title", "VARCHAR(128)", isNullable = false),
                    DatabaseColumn("framework", "VARCHAR(64)", isNullable = false),
                    DatabaseColumn("status", "VARCHAR(32)", defaultValue = "'ACTIVE'"),
                    DatabaseColumn("metadata", "JSONB", defaultValue = "'{}'::jsonb", isIndexed = true),
                    DatabaseColumn("updated_at", "TIMESTAMPTZ", defaultValue = "NOW()")
                ),
                sqlCreateStatement = """
                    CREATE TABLE projects (
                        id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                        user_id UUID REFERENCES users(id) ON DELETE CASCADE,
                        title VARCHAR(128) NOT NULL,
                        framework VARCHAR(64) NOT NULL,
                        status VARCHAR(32) DEFAULT 'ACTIVE',
                        metadata JSONB DEFAULT '{}'::jsonb,
                        updated_at TIMESTAMPTZ DEFAULT NOW()
                    );
                    CREATE INDEX idx_projects_user_id ON projects(user_id);
                    CREATE INDEX idx_projects_metadata_gin ON projects USING GIN (metadata);
                """.trimIndent()
            ),
            DatabaseTable(
                tableName = "agent_pipeline_runs",
                engine = DatabaseEngine.POSTGRESQL,
                rowCountEstimate = 3600L,
                sizeFormatted = "5.6 MB",
                columns = listOf(
                    DatabaseColumn("id", "UUID", isPrimaryKey = true),
                    DatabaseColumn("project_id", "UUID", isIndexed = true),
                    DatabaseColumn("assigned_agent", "VARCHAR(64)", isIndexed = true),
                    DatabaseColumn("status", "VARCHAR(32)", isIndexed = true),
                    DatabaseColumn("execution_ms", "INTEGER"),
                    DatabaseColumn("logs_payload", "TEXT")
                ),
                sqlCreateStatement = """
                    CREATE TABLE agent_pipeline_runs (
                        id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                        project_id UUID REFERENCES projects(id) ON DELETE CASCADE,
                        assigned_agent VARCHAR(64) NOT NULL,
                        status VARCHAR(32) NOT NULL,
                        execution_ms INTEGER NOT NULL,
                        logs_payload TEXT
                    );
                """.trimIndent()
            )
        )
    )
    val tables: StateFlow<List<DatabaseTable>> = _tables.asStateFlow()

    private val _queryHistory = MutableStateFlow<List<DatabaseQueryRecord>>(
        listOf(
            DatabaseQueryRecord(
                queryText = "SELECT u.username, p.title, p.framework FROM users u JOIN projects p ON u.id = p.user_id WHERE p.status = 'ACTIVE' LIMIT 5;",
                executionTimeMs = 3L,
                rowsAffected = 5,
                resultsJson = """[
  {"username": "darkvirgoyt", "title": "Valkyrie Open World", "framework": "Unreal Engine 5.4"},
  {"username": "developer", "title": "SaaS Cloud Workspace", "framework": "Next.js 15"},
  {"username": "admin", "title": "FastAPI Vector Hub", "framework": "FastAPI"}
]"""
            )
        )
    )
    val queryHistory: StateFlow<List<DatabaseQueryRecord>> = _queryHistory.asStateFlow()

    private val _migrations = MutableStateFlow<List<MigrationPlan>>(
        listOf(
            MigrationPlan(
                version = "20260831_001",
                title = "Initial Schema: Users & Projects with GIN indexes",
                upSql = "CREATE TABLE users (...); CREATE TABLE projects (...);",
                downSql = "DROP TABLE IF EXISTS projects; DROP TABLE IF EXISTS users;",
                status = "APPLIED"
            ),
            MigrationPlan(
                version = "20260831_002",
                title = "Add Vector Embeddings Column to Projects",
                upSql = "ALTER TABLE projects ADD COLUMN embedding vector(1536);",
                downSql = "ALTER TABLE projects DROP COLUMN embedding;",
                status = "APPLIED"
            )
        )
    )
    val migrations: StateFlow<List<MigrationPlan>> = _migrations.asStateFlow()

    fun selectEngine(engine: DatabaseEngine) {
        _selectedEngine.value = engine
    }

    fun executeQuery(sqlOrCommand: String): DatabaseQueryRecord {
        val execTime = (1..5).random().toLong()
        val record = DatabaseQueryRecord(
            queryText = sqlOrCommand,
            executionTimeMs = execTime,
            rowsAffected = 12,
            resultsJson = """{
  "status": "SUCCESS",
  "engine": "${_selectedEngine.value.displayName}",
  "latency": "${execTime}ms",
  "data": [
    {"id": "c7a8-4e12", "metric": "cpu_allocation", "value": "A100_SXM4_80GB", "health": "OPTIMAL"},
    {"id": "b9f1-33da", "metric": "vector_dimensions", "value": "1536", "health": "INDEXED"}
  ]
}"""
        )
        _queryHistory.value = listOf(record) + _queryHistory.value
        return record
    }

    fun generateNewTable(name: String, schemaDef: String) {
        val newTable = DatabaseTable(
            tableName = name.lowercase().replace(" ", "_"),
            engine = _selectedEngine.value,
            rowCountEstimate = 0L,
            sizeFormatted = "8 KB",
            columns = listOf(
                DatabaseColumn("id", "UUID", isPrimaryKey = true, isIndexed = true),
                DatabaseColumn("name", "VARCHAR(255)", isNullable = false),
                DatabaseColumn("data", "JSONB", defaultValue = "'{}'::jsonb"),
                DatabaseColumn("created_at", "TIMESTAMPTZ", defaultValue = "NOW()")
            ),
            sqlCreateStatement = schemaDef.ifBlank {
                """CREATE TABLE $name (id UUID PRIMARY KEY DEFAULT gen_random_uuid(), name VARCHAR(255) NOT NULL, created_at TIMESTAMPTZ DEFAULT NOW());"""
            }
        )
        _tables.value = _tables.value + newTable
    }
}
