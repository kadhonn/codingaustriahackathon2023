package at.nicerpricer.backend.model

data class ShoppingTrip(
        val stops: List<ShoppingStop>?,
        val unreachable: Map<GroceryItem, List<String>>?
)

data class ShoppingStop(
        val items: List<GroceryItem>?,
        val stop: Place?
)
