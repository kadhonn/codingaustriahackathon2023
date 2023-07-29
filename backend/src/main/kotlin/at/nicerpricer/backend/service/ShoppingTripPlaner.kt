package at.nicerpricer.backend.service

import at.nicerpricer.backend.model.*
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse.BodyHandlers
import java.util.Optional


class ShoppingTripPlaner(
        private val googleApiKey: String,
        private val groceryList: GroceryList,
        private val data: List<Data>,
) {

    private val httpClient = HttpClient.newBuilder().build()
    private val objectMapper = jacksonObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

    private val stops = mutableMapOf<Place, MutableList<GroceryItem>>()
    private val notFound = mutableMapOf<GroceryItem, List<String>>()
    private val cache = mutableMapOf<String, Place>()

    fun calculate(): ShoppingTrip {
        val items = groceryList.items ?: emptyList()
        items.forEach eachItem@{ item ->
            val shops = rankShops(item)
            var stopFound = false
            shops.forEach eachShop@{ shop ->
                val stop = getClosestStore(groceryList.location!!.get(), shop.store!!)
                if (stop.isPresent) {
                    stopFound = true
                    val stopItems = stops[stop.get()] ?: mutableListOf()
                    stopItems.add(item)
                    stops[stop.get()] = stopItems
                    return@eachItem
                }
            }
            if (!stopFound) {
                notFound[item] = shops.map { shop -> shop.store as String }.toList()
            }
        }

        return buildReturnValue()
    }

    private fun buildReturnValue(): ShoppingTrip {
        return ShoppingTrip(stops.entries.map { ShoppingStop(it.value, it.key) }, notFound)
    }

    private fun rankShops(item: GroceryItem): Sequence<Data> {
        return data.asSequence()
                .filter { it.name.equals(item.name) }
                .sortedBy { it.price!! }
    }

    private fun getClosestStore(location: Location, key: String): Optional<Place> {
        val cachedPlace = cache[key]
        if (cachedPlace != null) {
            return Optional.of(cachedPlace)
        }
        val request = HttpRequest.newBuilder()
                .uri(URI.create("https://maps.googleapis.com/maps/api/place/nearbysearch/json?keyword=${key}&location=${location.latitude}%2C${location.longitude}&rankby=distance&type=supermarket&key=${googleApiKey}"))
                .build();

        val response = httpClient.send(request, BodyHandlers.ofString());
        val places = objectMapper.readValue(response.body(), PlaceResponse::class.java)
        if (places.results.isNotEmpty()) {
            val place = places.results[0]
            cache[key] = place
            return Optional.of(place)
        }
        return Optional.empty()
    }
}
