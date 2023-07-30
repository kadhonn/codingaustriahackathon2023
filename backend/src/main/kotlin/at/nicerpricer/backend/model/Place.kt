package at.nicerpricer.backend.model

import com.fasterxml.jackson.annotation.JsonProperty

data class PlaceResponse(
        val status: String,
        val results: Array<Place>
)


// TODO consider open now?
data class Place(
        val geometry: Geometry,
        val icon: String,
        val name: String,
        @JsonProperty("place_id") val placeId: String,
        val vicinity: String,
)

data class Geometry(
        val location: GeometryLocation
)

data class GeometryLocation(
        val lat: Double,
        val lng: Double
)