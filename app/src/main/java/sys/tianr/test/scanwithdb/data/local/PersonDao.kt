package sys.tianr.test.scanwithdb.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import sys.tianr.test.scanwithdb.data.model.Person

@Dao
interface PersonDao {

    @Query("SELECT * FROM people ORDER BY name ASC")
    fun getAllPeople(): Flow<List<Person>>

    @Query("SELECT * FROM people WHERE id = :id")
    suspend fun getPersonById(id: Int): Person?

    @Query("SELECT * FROM people WHERE barcode = :barcode")
    suspend fun getPersonByBarcode(barcode: String): Person?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertPerson(person: Person)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(people: List<Person>)

    @Update
    suspend fun updatePerson(person: Person)

    @Delete
    suspend fun deletePerson(person: Person)

    @Query("DELETE FROM people")
    suspend fun deleteAll()

    @Query("UPDATE people SET isMarked = 0")
    suspend fun resetAllMarkedStatus()

    @Query("SELECT * FROM people WHERE isMarked = 1 ORDER BY name ASC")
    fun getMarkedPeople(): Flow<List<Person>>

    @Query("SELECT * FROM people WHERE isMarked = 0 ORDER BY name ASC")
    fun getUnmarkedPeople(): Flow<List<Person>>
}