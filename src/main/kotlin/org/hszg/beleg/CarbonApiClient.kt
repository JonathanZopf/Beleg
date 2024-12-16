package org.hszg.beleg

import org.hszg.beleg.carbon_event.FlightLeg
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import javax.print.attribute.standard.PrinterMoreInfoManufacturer

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
    fun estimateVehicleCarbon(
        distanceValue: Double,
        distanceUnit: String,
        vehicleManufacturer: String,
        modelName: String,
        year: Int
    ): EstimateResponse {
        val payload = mapOf(
            "type" to "vehicle",
            "distance_value" to distanceValue,
            "distance_unit" to distanceUnit,
            "vehicle_model_id" to getVehicleModelId(
                getCarManufacturerMakeId(vehicleManufacturer),
                modelName,
                year
            )
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

    /**
     * Fetch the vehicle make ID for a car manufaq
     */
    private fun getCarManufacturerMakeId(name: String): String {
        val url = "https://www.carboninterface.com/api/v1/vehicle_makes"

        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
            set("Authorization", "Bearer $apiKey")
        }

        val requestEntity = HttpEntity<Void>(headers)

        // Use a `ParameterizedTypeReference` to handle the List type
        val response = restTemplate.exchange(
            url,
            HttpMethod.GET,
            requestEntity,
            object : ParameterizedTypeReference<List<VehicleMakesResponse>>() {}
        )

        // Map through the list to find the required make
        return response.body?.firstOrNull { it.data.attributes.name == name }?.data?.id
            ?: throw IllegalStateException("Vehicle make ID not found for $name")
    }


    /**
     * Fetch the vehicle model ID for a specific make, model name, and year.
     */
    private fun getVehicleModelId(makeId: String, modelName: String, year: Int): String {
        val url = "https://www.carboninterface.com/api/v1/vehicle_makes/$makeId/vehicle_models"

        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
            set("Authorization", "Bearer $apiKey")
        }

        val requestEntity = HttpEntity<Void>(headers)

        val response = restTemplate.exchange(
            url,
            HttpMethod.GET,
            requestEntity,
            object : ParameterizedTypeReference<List<VehicleModelsResponse>>() {}
        )

        return response
            .body?.firstOrNull { it.data.attributes.name == modelName && it.data.attributes.year == year }?.data?.id
            ?: throw IllegalStateException("Vehicle model ID not found for $modelName")
    }

    /**
     * Data classes for parsing vehicle makes response.
     */
    data class VehicleMakesResponse(val data: VehicleMake)

    data class VehicleMake(
        val id: String,
        val attributes: VehicleMakeAttributes
    )

    data class VehicleMakeAttributes(
        val name: String,
        val number_of_models: Int
    )

    /**
     * Data classes for parsing vehicle models response.
     */
    data class VehicleModelsResponse(val data: VehicleModel)
    data class VehicleModel(val id: String, val attributes: VehicleModelAttributes)
    data class VehicleModelAttributes(val name: String, val year: Int)
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
