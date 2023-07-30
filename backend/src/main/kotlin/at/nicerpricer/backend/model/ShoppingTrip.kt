package at.nicerpricer.backend.model

import java.math.BigDecimal

data class ShoppingTrip(
        val stops: List<ShoppingStop>?,
        val unreachable: Map<GroceryItem, List<String>>?,
        val totalPrice: BigDecimal,
)

data class ShoppingStop(
        val items: List<GroceryItem>?,
        val stop: Place?
)
