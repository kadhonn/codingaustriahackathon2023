package at.nicerpricer.backend.model

import java.util.Optional

data class GroceryList(
        val items: List<GroceryItem>?,
        val location: Optional<Location>?,
)

data class Location(
        val latitude: Double,
        val longitude: Double
)

data class GroceryItem(
        val name: String?,
        val quantity: Int?,
)

data class QueryItem(
        val name: String?,
        val isCategory: Boolean?,
)
