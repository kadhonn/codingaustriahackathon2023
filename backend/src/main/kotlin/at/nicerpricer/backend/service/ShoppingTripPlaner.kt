package at.nicerpricer.backend.service

import at.nicerpricer.backend.model.*
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.math.BigDecimal
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse.BodyHandlers
import java.util.*

class ShoppingTripPlaner(
    private val googleApiKey: String,
    private val groceryList: GroceryList,
    private val data: PreparedDataHolder,
    private val categories: Map<String, List<String>>,
) {

    private val httpClient = HttpClient.newBuilder().build()
    private val objectMapper = jacksonObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

    private val notFound = mutableMapOf<GroceryItem, List<String>>()
    private val placeCache = mutableMapOf<String, Optional<Place>>()
    private val allStores = mutableListOf<String>()

    fun calculate(): List<ShoppingTrip> {
        var items = groceryList.items ?: emptyList()

        items.forEach { item ->
            val stores = allPossibleStoresSorted(item)
            stores.forEach {
                if (!allStores.contains(it.store)) {
                    allStores.add(it.store!!)
                }
            }
        }
        val stops = findBestTripForPossibleStops(allStores.toList(), items)!!

        items = items.filter { !notFound.containsKey(it) }

        val results = mutableListOf<ShoppingTrip>()
        results.add(buildReturnValue(stops))
        val maxStopsSize = stops.size
        //TODO filter best against bigger ones
        for (stopsSize in (maxStopsSize - 1) downTo 1) {
            val result = findBestTripForStopsSize(stopsSize, items)
            if (result != null) {
                results.add(result)
            }
        }

        return results
    }

    private fun findBestTripForPossibleStops(
        possibleStores: List<String>,
        items: List<GroceryItem>
    ): MutableMap<Place, MutableList<Pair<GroceryItem, BigDecimal>>>? {
        val stops = mutableMapOf<Place, MutableList<Pair<GroceryItem, BigDecimal>>>()
        items.forEach { item ->
            val stores = rankStores(possibleStores, item)
            if (stores.isEmpty()) {
                return null
            }
            val place = findFirstExistingPlaceWithData(stores)
            if (place.isPresent) {
                val stopItems = stops[place.get().first] ?: mutableListOf()
                stopItems.add(Pair(GroceryItem(place.get().second.name, item.quantity), place.get().second.price!!))
                stops[place.get().first] = stopItems
            } else {
                notFound[item] = stores.map { shop -> shop.store as String }.toList()
            }
        }
        return stops
    }

    private fun findBestTripForStopsSize(stopsSize: Int, items: List<GroceryItem>): ShoppingTrip? {
        val possibleTrips = mutableListOf<ShoppingTrip>()
        val possibleStoreCombinations = mutableListOf<List<String>>()
        calcAllPossibleStoreCombinations(stopsSize, possibleStoreCombinations, mutableListOf(), 0, 1)

        for (storeCombination in possibleStoreCombinations) {
            val stops = findBestTripForPossibleStops(storeCombination, items)
            if (stops != null) {
                possibleTrips.add(buildReturnValue(stops))
            }
        }
        return possibleTrips.sortedBy { it.totalPrice }.firstOrNull()
    }

    private fun calcAllPossibleStoreCombinations(
        stopsSize: Int,
        possibleStoreCombinations: MutableList<List<String>>,
        currentSelection: MutableList<String>,
        startI: Int,
        depth: Int
    ) {
        for (i in startI until allStores.size) {
            currentSelection.add(allStores[i])
            if (stopsSize == depth) {
                possibleStoreCombinations.add(currentSelection.toList())
            } else {
                calcAllPossibleStoreCombinations(
                    stopsSize,
                    possibleStoreCombinations,
                    currentSelection,
                    i + 1,
                    depth + 1
                )
            }
            currentSelection.removeAt(currentSelection.size - 1)
        }
    }

    private fun findFirstExistingPlaceWithData(stores: List<Data>): Optional<Pair<Place, Data>> {
        for (store in stores) {
            val place = getClosestPlace(groceryList.location!!.get(), store.store!!)
            if (place.isPresent) {
                return Optional.of(Pair(place.get(), store))
            } else {
                allStores.remove(store.store!!)
            }
        }
        return Optional.empty()
    }

    private fun buildReturnValue(stops: Map<Place, MutableList<Pair<GroceryItem, BigDecimal>>>): ShoppingTrip {
        return ShoppingTrip(
            stops.entries.map {
                ShoppingStop(
                    it.value.map { ShoppingItem(it.first.name, it.first.quantity, it.second) },
                    it.key
                )
            },
            notFound, //TODO is the same for all solutions, so we duplicate it right now
            stops.entries.sumOf {
                it.value.sumOf {
                    it.second.multiply(BigDecimal.valueOf(it.first.quantity!!.toDouble()))
                }
            }
        )
    }

    private fun rankStores(possibleStores: List<String>, item: GroceryItem): List<Data> {
        return allPossibleStoresSorted(item)
            .filter { possibleStores.contains(it.store) }
    }

    private fun allPossibleStoresSorted(item: GroceryItem): List<Data> {
        if (!categories.containsKey(item.name)) {
            return data.byName[item.name!!.lowercase()]!!
        } else {
            val category = categories[item.name]!!
            val result = data.stores.mapNotNull { store ->
                category.mapNotNull { categoryItem ->
                    data.byName[categoryItem.lowercase()]?.firstOrNull { it.store == store }
                }
                    .sortedBy { it.price }.firstOrNull()
            }
            return result
        }
    }

    private fun getClosestPlace(location: Location, key: String): Optional<Place> {
        val cachedPlace = placeCache[key]
        if (cachedPlace != null) {
            return cachedPlace
        }
        val request = HttpRequest.newBuilder()
            .uri(URI.create("https://maps.googleapis.com/maps/api/place/nearbysearch/json?keyword=${key}&location=${location.latitude}%2C${location.longitude}&rankby=distance&type=supermarket&key=${googleApiKey}"))
            .build();

        val response = httpClient.send(request, BodyHandlers.ofString());
        val places = objectMapper.readValue(response.body(), PlaceResponse::class.java)
        if (places.results.isNotEmpty()) {
            val place = places.results[0]
            placeCache[key] = Optional.of(place)
            return Optional.of(place)
        } else {
            placeCache[key] = Optional.empty()
            return Optional.empty()
        }
    }
}

