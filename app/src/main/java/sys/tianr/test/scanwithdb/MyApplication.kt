package sys.tianr.test.scanwithdb

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import sys.tianr.test.scanwithdb.data.DatabaseManager
import javax.inject.Inject

@HiltAndroidApp
class MyApplication : Application() {

    @Inject
    lateinit var databaseManager: DatabaseManager

    override fun onCreate() {
        super.onCreate()
        databaseManager.ensureDatabaseInitialized()
    }
}
