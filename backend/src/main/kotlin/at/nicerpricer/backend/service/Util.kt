package at.nicerpricer.backend.service

import at.nicerpricer.backend.model.Data
import at.nicerpricer.backend.model.PreparedDataHolder


object Util {
    fun allPossibleStoresSorted(
        name: String,
        categories: Map<String, List<String>>,
        data: PreparedDataHolder
    ): List<Data> {
        if (!categories.containsKey(name)) {
            return data.byName[name.lowercase()]!!
        } else {
            val category = categories[name]!!
            val result = data.stores.mapNotNull { store ->
                category.mapNotNull { categoryItem ->
                    data.byName[categoryItem.lowercase()]?.firstOrNull { it.store == store }
                }
                    .sortedBy { it.price }.firstOrNull()
            }
            return result
        }
    }
}