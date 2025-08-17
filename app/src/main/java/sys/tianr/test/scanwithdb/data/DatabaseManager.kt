package sys.tianr.test.scanwithdb.data

import android.content.Context
import android.util.Log
import androidx.room.Room
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import sys.tianr.test.scanwithdb.data.local.AppDatabase
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DatabaseManager @Inject constructor(@ApplicationContext private val context: Context) {

    private val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    private val _database = MutableStateFlow<AppDatabase?>(null)
    val database: StateFlow<AppDatabase?> = _database.asStateFlow()

    init {
        val initialDbName = prefs.getString("current_db", "default_database.db") ?: "default_database.db"
        Log.d("DatabaseManager", "Initializing with database: $initialDbName")
        // The switchDatabase call was removed from here to avoid issues with testability
        // and to give more control to the application's startup logic.
        // The database will be initialized on first use or by an explicit call.
    }

    fun switchDatabase(dbName: String, force: Boolean = false) {
        val currentDb = _database.value
        if (!force && currentDb?.openHelper?.databaseName == dbName && currentDb.isOpen) {
            Log.d("DatabaseManager", "Database '$dbName' is already open. No switch needed.")
            return
        }

        Log.d("DatabaseManager", "Switching database to '$dbName'")
        synchronized(this) {
            _database.value?.close()
            val newDb = createDb(context, dbName)
            _database.value = newDb
            prefs.edit().putString("current_db", dbName).apply()
            Log.d("DatabaseManager", "Switched database to '$dbName' successfully.")
        }
    }

    fun ensureDatabaseInitialized() {
        if (_database.value == null) {
            val initialDbName = prefs.getString("current_db", "default_database.db") ?: "default_database.db"
            switchDatabase(initialDbName, true)
        }
    }


    fun createNewDatabaseFile(dbName: String): Boolean {
        val dbFile = context.getDatabasePath(dbName)
        if (dbFile.exists()) {
            Log.w("DatabaseManager", "Database file '$dbName' already exists.")
            return false
        }
        // To ensure file creation, we get a writable database instance and then close it immediately.
        try {
            val db = createDb(context, dbName)
            db.openHelper.writableDatabase // This forces Room to create the .db, .db-shm, and .db-wal files.
            db.close()
            Log.d("DatabaseManager", "Successfully created database file: $dbName")
            return true
        } catch (e: Exception) {
            Log.e("DatabaseManager", "Failed to create database file: $dbName", e)
            return false
        }
    }

    private fun createDb(context: Context, dbName: String): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            dbName
        ).fallbackToDestructiveMigration()
         .build()
    }

    fun listDatabases(): List<String> {
        val dbFolder = context.getDatabasePath("any_db_name").parentFile ?: return emptyList()
        return dbFolder.listFiles { _, name -> name.endsWith(".db") }
            ?.map { it.name }
            ?.sorted()
            ?: emptyList()
    }

    fun deleteDatabase(dbName: String): Boolean {
        val dbFile = context.getDatabasePath(dbName)
        if (!dbFile.exists()) return true

        val wasActive = _database.value?.openHelper?.databaseName == dbName

        // 如果删除的是当前活动的数据库，我们需要切换到一个新的有效数据库
        if (wasActive) {
            val otherDb = listDatabases().firstOrNull { it != dbName }
            if (otherDb != null) {
                switchDatabase(otherDb)
            } else {
                // 这是最后一个数据库，关闭它，但不设置新的
                synchronized(this) {
                    _database.value?.close()
                    _database.value = null
                }
            }
        }

        // 删除物理文件
        try {
            val deleted = dbFile.delete()
            if (deleted) {
                File(dbFile.parent, "$dbName-shm").delete()
                File(dbFile.parent, "$dbName-wal").delete()
                Log.d("DatabaseManager", "Successfully deleted database '$dbName' and its helper files.")
            }
            return deleted
        } catch (e: SecurityException) {
            Log.e("DatabaseManager", "Failed to delete database '$dbName'", e)
            return false
        }
    }
}