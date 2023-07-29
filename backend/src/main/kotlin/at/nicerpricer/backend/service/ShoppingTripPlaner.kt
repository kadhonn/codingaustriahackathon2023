package at.nicerpricer.backend.service

import at.nicerpricer.backend.model.*

class ShoppingTripPlaner(
    private val groceryList: GroceryList,
    private val data: List<Data>
) {

    private val stops = mutableMapOf<String, MutableList<GroceryItem>>()

    fun calculate(): ShoppingTrip {
        for (item in groceryList.items ?: emptyList()) {
            calculateItem(item)
        }

        return buildReturnValue()
    }

    private fun buildReturnValue(): ShoppingTrip {
        return ShoppingTrip(stops.entries.map { ShoppingStop(it.value, it.key) })
    }

    private fun calculateItem(item: GroceryItem) {
        val cheapestData = findCheapestDataForItem(item)
        if (!stops.containsKey(cheapestData.store!!)) {
            stops[cheapestData.store] = mutableListOf()
        }
        stops[cheapestData.store]!!.add(item)
    }

    private fun findCheapestDataForItem(item: GroceryItem): Data {
        return data.asSequence()
            .filter { it.name.equals(item.name) }
            .minBy { it.price!! }
    }

}
