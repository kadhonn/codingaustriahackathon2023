package at.nicerpricer.backend.service

import at.nicerpricer.backend.model.Data
import at.nicerpricer.backend.model.GroceryList
import at.nicerpricer.backend.model.ShoppingTrip
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
            .mapNotNull { it.name }
            .filter { it.contains(nameQuery, true) }
            .take(10)
            .toList()
    }

    fun shop(groceryList: GroceryList): ShoppingTrip {
        return ShoppingTripPlaner(groceryList, data).calculate()
    }

    private fun loadData(filePath: String): List<Data> {
        return JACKSON_MAPPER.readValue(
            File(filePath),
            object : TypeReference<List<Data>>() {})
    }
}