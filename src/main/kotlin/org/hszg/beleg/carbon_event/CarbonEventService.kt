package org.hszg.beleg.carbon_event

import jakarta.persistence.EntityNotFoundException
import org.hszg.beleg.CarbonApiClient
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.util.*
import javax.print.attribute.standard.PrinterMoreInfoManufacturer

@Service
class CarbonEventService (
    private val carbonEventRepository: CarbonEventRepository,
    private val carbonApiClient: CarbonApiClient
) {

    /**
     * Gets aa CarbonEvent by its id.
     *
     * @param id The id of the CarbonEvent to get.
     * @return The CarbonEventEntity with the given id.
     */
    fun getCarbonEventById(id: UUID): CarbonEventEntity {
        return carbonEventRepository.findById(id)
            .orElseThrow { EntityNotFoundException("CarbonEvent with id $id not found") }
    }

    /**
     * Get all CarbonEvents in a given time range.
     *
     * @param start The start of the time range. Inclusive.
     * @param end The end of the time range. Inclusive.
     * @return A list of CarbonEventEntity objects that are in the given time range.
     */
    fun getCarbonEventsInTimeRange(start: LocalDate, end: LocalDate): List<CarbonEventEntity> {
        return carbonEventRepository.findByDateIsBetween(start, end)
    }

    /**
     * Get the total amount of carbon emitted by all CarbonEvents in a given time range.
     *
     * @param start The start of the time range. Inclusive.
     * @param end The end of the time range. Inclusive.
     * @return The total amount of carbon emitted in g in the given time range.
     */
    fun accumulateCarbonEventsInTimeRange(start: LocalDate, end: LocalDate): Int {
        return carbonEventRepository.findByDateIsBetween(start, end).sumOf { it.amount }
    }

    /**
     * Get the total amount of carbon emitted for the specific CarbonEventType in a given time range.
     *
     * @param start The start of the time range. Inclusive.
     * @param end The end of the time range. Inclusive.
     * @return A list of pairs where the first element is the CarbonEventType and the second element is the total amount of carbon emitted by that type in the given time range.
     */
    fun accumulateCarbonEventsInTimeRangeByType(start: LocalDate, end: LocalDate): List<Pair<CarbonEventType, Int>> {
        return CarbonEventType.entries.map { type ->
            Pair(type, carbonEventRepository.findByTypeAndDateIsBetween(type, start, end).sumOf { it.amount })
        }
    }

    /**
     * Create a new CarbonEvent. The id of the CarbonEventEntity will be generated.
     *
     * @param createCarbonEventDTO The DTO containing the data to create the CarbonEvent. See [CreateUpdateCarbonEventDTO].
     * @return The created CarbonEventEntity.
     */
    fun createCarbonEvent(createCarbonEventDTO: CreateUpdateCarbonEventDTO): CarbonEventEntity {
        val carbonEvent = CarbonEventEntity(
            UUID.randomUUID(),
            createCarbonEventDTO.type,
            createCarbonEventDTO.date,
            createCarbonEventDTO.amount
        )
        return carbonEventRepository.save(carbonEvent)
    }

    /**
     * Create a flight CarbonEvent. The id of the CarbonEventEntity will be generated.
     * The amount of carbon emitted will be calculated based on data provided by the API.
     *
     * @param passengers The number of passengers to calculate the carbon emission for.
     * @param legs The legs of the flight. Each leg is a flight from one airport to another.
     * @return The created CarbonEventEntity.
     */
    fun createFlightCarbonEvent(
        passengers: Int,
        legs: List<FlightLeg>
    ): CarbonEventEntity {
        val response = carbonApiClient.estimateFlightCarbon(
            passengers = passengers,
            legs = legs
        )
        val carbonAmount = response.carbon_g
        val carbonEvent = CarbonEventEntity(
            id = UUID.randomUUID(),
            type = CarbonEventType.FLIGHT,
            date = LocalDate.now(),
            amount = carbonAmount
        )
        return carbonEventRepository.save(carbonEvent)
    }

    /**
     * Create a car CarbonEvent. The id of the CarbonEventEntity will be generated.
     * The amount of carbon emitted will be calculated based on data provided by the API.
     *
     * @param distanceValue The distance the car traveled. Measured in km.
     * @param vehicleManufacturer The manufacturer of the car used.
     * @param vehicleModelName The model name of the car used.
     * @param vehicleYear The year the car was manufactured.
     * @return The created CarbonEventEntity.
     */
    fun createCarCarbonEvent(
        distanceValue: Double,
        vehicleManufacturer: String,
        vehicleModelName: String,
        vehicleYear: Int
    ): CarbonEventEntity {
        val response = carbonApiClient.estimateVehicleCarbon(
            distanceValue = distanceValue,
            distanceUnit = "km",
            vehicleManufacturer = vehicleManufacturer,
            modelName = vehicleModelName,
            year = vehicleYear
        )
        val carbonAmount = response.carbon_g
        val carbonEvent = CarbonEventEntity(
            id = UUID.randomUUID(),
            type = CarbonEventType.CAR,
            date = LocalDate.now(),
            amount = carbonAmount
        )
        return carbonEventRepository.save(carbonEvent)
    }

    /**
     * Create a shipping CarbonEvent. The id of the CarbonEventEntity will be generated.
     * The amount of carbon emitted will be calculated based on data provided by the API.
     *
     * @param weightValue The weight of the shipment. Measured in kg.
     * @param distanceValue The distance the shipment traveled. Measured in km.
     * @param transportMethod The method of transport used for the shipment.
     * @return The created CarbonEventEntity.
     */
    fun createShippingCarbonEvent(
        weightValue: Double,
        distanceValue: Double,
        transportMethod: String
    ): CarbonEventEntity {
        val response = carbonApiClient.estimateShippingCarbon(
            weightValue = weightValue,
            weightUnit = "kg",
            distanceValue = distanceValue,
            distanceUnit = "km",
            transportMethod = transportMethod
        )
        val carbonAmount = response.carbon_g
        val carbonEvent = CarbonEventEntity(
            id = UUID.randomUUID(),
            type = CarbonEventType.SHIPPING,
            date = LocalDate.now(),
            amount = carbonAmount
        )
        return carbonEventRepository.save(carbonEvent)
    }

    /**
     * Update an existing CarbonEvent.
     *
     * @param id The id of the CarbonEvent to update.
     * @param updateCarbonEventDTO The DTO containing the data to update the CarbonEvent. See [CreateUpdateCarbonEventDTO].
     * @return The updated CarbonEventEntity.
     */
    fun updateCarbonEvent(id: UUID, updateCarbonEventDTO: CreateUpdateCarbonEventDTO): CarbonEventEntity {
        val carbonEvent = carbonEventRepository.findById(id)
            .orElseThrow { EntityNotFoundException("CarbonEvent with id $id not found") }
        carbonEvent.type = updateCarbonEventDTO.type
        carbonEvent.date = updateCarbonEventDTO.date
        carbonEvent.amount = updateCarbonEventDTO.amount
        return carbonEventRepository.save(carbonEvent)
    }

    /**
     * Delete a CarbonEvent by its id.
     *
     * @param id The id of the CarbonEvent to delete.
     */
    fun deleteCarbonEvent(id: UUID) {
        carbonEventRepository.deleteById(id)
    }

    /**
     * Delete all CarbonEvents in a given time range.
     *
     * @param start The start of the time range. Inclusive.
     * @param end The end of the time range. Inclusive.
     */
    fun deleteCarbonEventsInTimeRange(start: LocalDate, end: LocalDate) {
        carbonEventRepository.deleteAll(carbonEventRepository.findByDateIsBetween(start, end))
    }
}