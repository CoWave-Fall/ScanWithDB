package sys.tianr.test.scanwithdb.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import sys.tianr.test.scanwithdb.data.model.Person

@Database(entities = [Person::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun personDao(): PersonDao
}