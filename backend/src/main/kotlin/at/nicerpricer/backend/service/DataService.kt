package at.nicerpricer.backend.service

import at.nicerpricer.backend.model.Data
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.io.File

val JACKSON_MAPPER: ObjectMapper = ObjectMapper()
    .registerModule(KotlinModule.Builder().build())
    .registerModule(JavaTimeModule())

@Service
class DataService(
    @Value("\${nicerpricer.file.path}") filePath: String,
) {
    private val data: List<Data> = loadData(filePath)

    fun first(): Data {
        return data.first()
    }

    fun query(nameQuery: String): List<String> {
        return data.asSequence()
            .filter { it.name!!.contains(nameQuery, true) }
            .mapNotNull { it.name }
            .toList()
    }

    private fun loadData(filePath: String): List<Data> {
        return JACKSON_MAPPER.readValue(
            File(filePath),
            object : TypeReference<List<Data>>() {})
    }
}