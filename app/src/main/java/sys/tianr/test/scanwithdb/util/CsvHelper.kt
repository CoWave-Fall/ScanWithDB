package sys.tianr.test.scanwithdb.util

import sys.tianr.test.scanwithdb.data.model.Person
import java.io.BufferedReader
import java.io.OutputStream
import java.io.OutputStreamWriter

object CsvHelper {

    fun readCsv(reader: BufferedReader): List<Person> {
        val people = mutableListOf<Person>()
        reader.useLines { lines ->
            lines.drop(1) // Drop header row
                .forEach { line ->
                    val tokens = line.split(",")
                    if (tokens.size >= 2) {
                        val person = Person(name = tokens[0], barcode = tokens[1])
                        people.add(person)
                    }
                }
        }
        return people
    }

    fun writeCsv(outputStream: OutputStream, people: List<Person>) {
        val writer = OutputStreamWriter(outputStream)
        writer.use {
            // Write header
            it.append("Name,Barcode\n")
            // Write data
            people.forEach { person ->
                it.append("${person.name},${person.barcode}\n")
            }
        }
    }
}