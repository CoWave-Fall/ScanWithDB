package sys.tianr.test.scanwithdb.ui.data_management

import android.content.Context
import android.net.Uri
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import sys.tianr.test.scanwithdb.data.model.Person
import sys.tianr.test.scanwithdb.data.repository.PersonRepository
import sys.tianr.test.scanwithdb.util.CsvHelper
import java.io.BufferedReader
import java.io.InputStreamReader
import javax.inject.Inject

@HiltViewModel
class DataViewModel @Inject constructor(
    private val repository: PersonRepository
) : ViewModel() {

    val allPeople: LiveData<List<Person>> = repository.getAllPeople().asLiveData()

    fun addPerson(name: String, barcode: String) = viewModelScope.launch {
        repository.insertPerson(Person(name = name, barcode = barcode))
    }

    fun updatePerson(person: Person) = viewModelScope.launch {
        repository.updatePerson(person)
    }

    fun deletePerson(person: Person) = viewModelScope.launch {
        repository.deletePerson(person)
    }

    fun importFromCsv(context: Context, uri: Uri) = viewModelScope.launch {
        try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val reader = BufferedReader(InputStreamReader(inputStream))
            val people = CsvHelper.readCsv(reader)
            repository.insertAll(people)
        } catch (e: Exception) {
            // Handle exceptions
            e.printStackTrace()
        }
    }

    fun exportToCsv(context: Context, uri: Uri) = viewModelScope.launch {
        try {
            val people = allPeople.value ?: emptyList()
            context.contentResolver.openOutputStream(uri)?.let {
                CsvHelper.writeCsv(it, people)
            }
        } catch (e: Exception) {
            // Handle exceptions
            e.printStackTrace()
        }
    }
}