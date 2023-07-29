package at.nicerpricer.backend.model

data class ShoppingTrip(
    val stops: List<ShoppingStop>?
)

data class ShoppingStop(
    val items: List<GroceryItem>?,
    val shop: String?
)
