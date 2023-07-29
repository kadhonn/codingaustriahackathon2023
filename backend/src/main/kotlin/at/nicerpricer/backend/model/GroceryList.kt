package at.nicerpricer.backend.model

import java.util.Optional

data class GroceryList(
    val items: List<GroceryItem>?,
    val location: Optional<String>?, //TODO
)

data class GroceryItem(
    val productName: String?,
    val quantity: Int?,
)
