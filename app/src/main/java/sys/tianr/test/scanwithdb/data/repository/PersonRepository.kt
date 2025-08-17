package sys.tianr.test.scanwithdb.data.repository

import kotlinx.coroutines.flow.Flow
import sys.tianr.test.scanwithdb.data.model.Person

interface PersonRepository {
    fun getAllPeople(): Flow<List<Person>>
    suspend fun getPersonByBarcode(barcode: String): Person?
    suspend fun insertPerson(person: Person)
    suspend fun insertAll(people: List<Person>)
    suspend fun updatePerson(person: Person)
    suspend fun deletePerson(person: Person)
    suspend fun deleteAll()
    suspend fun resetAllMarkedStatus()
    fun getMarkedPeople(): Flow<List<Person>>
    fun getUnmarkedPeople(): Flow<List<Person>>
}