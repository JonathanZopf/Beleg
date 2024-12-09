package org.hszg.beleg

import org.hszg.beleg.carbon_event.FlightLeg
import org.springframework.context.annotation.ComponentScan
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

@Component
class CarbonApiClient(private val restTemplate: RestTemplate) {

    private val apiKey: String = "iqgJ7wx1bY7SZ9bOJ73QeQ"
    private val baseUrl: String = "https://www.carboninterface.com/api/v1/estimates"

    /**
     * Estimate carbon emissions for a flight.
     */
    fun estimateFlightCarbon(passengers: Int, legs: List<FlightLeg>): EstimateResponse {
        val payload = mapOf(
            "type" to "flight",
            "passengers" to passengers,
            "legs" to legs.map { it.toMap() }
        )

        return makePostRequest(payload)
    }

    /**
     * Estimate carbon emissions for a vehicle trip.
     */
    fun estimateVehicleCarbon(distanceValue: Double, distanceUnit: String, vehicleModelId: String): EstimateResponse {
        val payload = mapOf(
            "type" to "vehicle",
            "distance_value" to distanceValue,
            "distance_unit" to distanceUnit,
            "vehicle_model_id" to vehicleModelId
        )

        return makePostRequest(payload)
    }

    /**
     * Estimate carbon emissions for shipping.
     */
    fun estimateShippingCarbon(
        weightValue: Double,
        weightUnit: String,
        distanceValue: Double,
        distanceUnit: String,
        transportMethod: String
    ): EstimateResponse {
        val payload = mapOf(
            "type" to "shipping",
            "weight_value" to weightValue,
            "weight_unit" to weightUnit,
            "distance_value" to distanceValue,
            "distance_unit" to distanceUnit,
            "transport_method" to transportMethod
        )

        return makePostRequest(payload)
    }

    private fun makePostRequest(payload: Map<String, Any>): EstimateResponse {
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        headers.set("Authorization", "Bearer $apiKey")

        val requestEntity = HttpEntity(payload, headers)

        val response = restTemplate.exchange(
            baseUrl,
            HttpMethod.POST,
            requestEntity,
            EstimateResponse::class.java
        )

        return response.body ?: throw IllegalStateException("API returned no response")
    }
}

data class EstimateResponse(
    val data: EstimateData
) {
    val carbon_g: Int get() = data.attributes.carbon_g
}

data class EstimateData(
    val id: String,
    val type: String,
    val attributes: EstimateAttributes
)

data class EstimateAttributes(
    val carbon_g: Int,
    val carbon_lb: Double,
    val carbon_kg: Double,
    val carbon_mt: Double,
    val estimated_at: String
)
