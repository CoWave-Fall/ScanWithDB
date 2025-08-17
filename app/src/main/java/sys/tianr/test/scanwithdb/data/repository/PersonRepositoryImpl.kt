package sys.tianr.test.scanwithdb.data.repository

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import sys.tianr.test.scanwithdb.data.DatabaseManager
import sys.tianr.test.scanwithdb.data.local.PersonDao
import sys.tianr.test.scanwithdb.data.model.Person
import javax.inject.Inject
import javax.inject.Singleton

@OptIn(ExperimentalCoroutinesApi::class)
@Singleton
class PersonRepositoryImpl @Inject constructor(
    private val databaseManager: DatabaseManager
) : PersonRepository {

    init {
        // Ensure the database is initialized when the repository is first created.
        databaseManager.ensureDatabaseInitialized()
    }

    override fun getAllPeople(): Flow<List<Person>> = databaseManager.database.flatMapLatest { db ->
        db?.personDao()?.getAllPeople() ?: flowOf(emptyList())
    }

    override fun getMarkedPeople(): Flow<List<Person>> = databaseManager.database.flatMapLatest { db ->
        db?.personDao()?.getMarkedPeople() ?: flowOf(emptyList())
    }

    override fun getUnmarkedPeople(): Flow<List<Person>> = databaseManager.database.flatMapLatest { db ->
        db?.personDao()?.getUnmarkedPeople() ?: flowOf(emptyList())
    }

    private suspend fun <T> withDao(block: suspend (PersonDao) -> T): T {
        // Wait for the database to be non-null and ready
        val db = databaseManager.database.first { it != null && it.isOpen }!!
        return block(db.personDao())
    }

    override suspend fun getPersonByBarcode(barcode: String): Person? = withDao { dao ->
        dao.getPersonByBarcode(barcode)
    }

    override suspend fun insertPerson(person: Person) = withDao { dao ->
        dao.insertPerson(person)
    }

    override suspend fun insertAll(people: List<Person>) = withDao { dao ->
        dao.insertAll(people)
    }

    override suspend fun updatePerson(person: Person) = withDao { dao ->
        dao.updatePerson(person)
    }

    override suspend fun deletePerson(person: Person) = withDao { dao ->
        dao.deletePerson(person)
    }

    override suspend fun deleteAll() = withDao { dao ->
        dao.deleteAll()
    }

    override suspend fun resetAllMarkedStatus() = withDao { dao ->
        dao.resetAllMarkedStatus()
    }
}
