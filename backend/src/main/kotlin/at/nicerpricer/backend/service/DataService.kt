package at.nicerpricer.backend.service

import at.nicerpricer.backend.model.Data
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.springframework.stereotype.Service
import java.io.File

val JACKSON_MAPPER = ObjectMapper()
    .registerModule(KotlinModule.Builder().build())
    .registerModule(JavaTimeModule())

@Service
class DataService {
    private val data: List<Data> = loadData()


    fun first(): Data {
        return data.first()
    }


    private fun loadData(): List<Data> {
        return JACKSON_MAPPER.readValue(
            File("C:\\projects\\codingaustriahackathon2023\\latest-canonical.json"),
            object : TypeReference<List<Data>>() {})
    }
}