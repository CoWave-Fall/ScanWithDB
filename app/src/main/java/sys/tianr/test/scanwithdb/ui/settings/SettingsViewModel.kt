package sys.tianr.test.scanwithdb.ui.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import sys.tianr.test.scanwithdb.data.DatabaseManager
import sys.tianr.test.scanwithdb.data.repository.PersonRepository
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: PersonRepository,
    private val databaseManager: DatabaseManager
) : ViewModel() {

    fun clearAllMarkedStatuses() {
        viewModelScope.launch {
            repository.resetAllMarkedStatus()
        }
    }

    fun clearAllData() {
        viewModelScope.launch {
            repository.deleteAll()
        }
    }

    fun getDatabaseList(): List<String> {
        return databaseManager.listDatabases()
    }

    fun deleteDatabase(dbName: String): Boolean {
        return databaseManager.deleteDatabase(dbName)
    }
}