package org.hszg.beleg.carbon_event



data class FlightLeg(
    val departureAirport: String,
    val destinationAirport: String,
    val cabinClass: String? = null
) {
    fun toMap(): Map<String, String> {
        val map = mutableMapOf(
            "departure_airport" to departureAirport,
            "destination_airport" to destinationAirport
        )
        cabinClass?.let { map["cabin_class"] = it }
        return map
    }
}