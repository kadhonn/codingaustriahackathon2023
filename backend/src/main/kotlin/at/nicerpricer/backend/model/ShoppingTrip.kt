package at.nicerpricer.backend.model

data class ShoppingTrip(
        val stops: List<ShoppingStop>?,
        val unreachable: List<GroceryItem>?
)

data class ShoppingStop(
        val items: List<GroceryItem>?,
        val stop: Place?
)
