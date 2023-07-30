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
) {

    private val httpClient = HttpClient.newBuilder().build()
    private val objectMapper = jacksonObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

    private val notFound = mutableMapOf<GroceryItem, List<String>>()
    private val placeCache = mutableMapOf<String, Optional<Place>>()
    private val allPlaces = mutableSetOf<String>()

    fun calculate(): List<ShoppingTrip> {
        val items = groceryList.items ?: emptyList()
        val stops = mutableMapOf<Place, MutableList<Pair<GroceryItem, BigDecimal>>>()
        items.forEach { item ->
            val stores = rankStores(item)
            stores.forEach { allPlaces.add(it.store!!) }

            val place = findFirstExistingPlaceWithPrice(stores)
            if (place.isPresent) {
                val stopItems = stops[place.get().first] ?: mutableListOf()
                stopItems.add(Pair(item, place.get().second))
                stops[place.get().first] = stopItems
            } else {
                notFound[item] = stores.map { shop -> shop.store as String }.toList()
            }
        }

        return listOf(buildReturnValue(stops))
    }

    private fun findFirstExistingPlaceWithPrice(stores: List<Data>): Optional<Pair<Place, BigDecimal>> {
        for (store in stores) {
            val place = getClosestPlace(groceryList.location!!.get(), store.store!!)
            if (place.isPresent) {
                return Optional.of(Pair(place.get(), store.price!!))
            }
        }
        return Optional.empty()
    }

    private fun buildReturnValue(stops: Map<Place, MutableList<Pair<GroceryItem, BigDecimal>>>): ShoppingTrip {
        return ShoppingTrip(
            stops.entries.map { ShoppingStop(it.value.map { it.first }, it.key) },
            notFound, //TODO is the same for all solutions, so we duplicate it right now
            stops.entries.sumOf {
                it.value.sumOf {
                    it.second.multiply(BigDecimal.valueOf(it.first.quantity!!.toDouble()))
                }
            }
        )
    }

    private fun rankStores(item: GroceryItem): List<Data> {
        return data.byName[item.name!!.lowercase()]!!
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
