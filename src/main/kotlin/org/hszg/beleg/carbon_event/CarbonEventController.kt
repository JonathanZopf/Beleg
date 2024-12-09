package org.hszg.beleg.carbon_event

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/v1/carbon-events")
class CarbonEventController(
    private val carbonEventService: CarbonEventService
) {

    @GetMapping("/{id}")
    fun getCarbonEventById(@PathVariable id: UUID): ResponseEntity<CarbonEventEntity> {
        val event = carbonEventService.getCarbonEventById(id)
        return ResponseEntity.ok(event)
    }

    @GetMapping
    fun getCarbonEventsInTimeRange(
        @RequestParam start: Date,
        @RequestParam end: Date
    ): ResponseEntity<List<CarbonEventEntity>> {
        val events = carbonEventService.getCarbonEventsInTimeRange(start, end)
        return ResponseEntity.ok(events)
    }

    @GetMapping("/accumulate")
    fun accumulateCarbonEventsInTimeRange(
        @RequestParam start: Date,
        @RequestParam end: Date
    ): ResponseEntity<Int> {
        val totalCarbon = carbonEventService.accumulateCarbonEventsInTimeRange(start, end)
        return ResponseEntity.ok(totalCarbon)
    }

    @GetMapping("/accumulate/by-type")
    fun accumulateCarbonEventsInTimeRangeByType(
        @RequestParam start: Date,
        @RequestParam end: Date
    ): ResponseEntity<List<Pair<CarbonEventType, Int>>> {
        val result = carbonEventService.accumulateCarbonEventsInTimeRangeByType(start, end)
        return ResponseEntity.ok(result)
    }

    @PostMapping
    fun createCarbonEvent(@RequestBody createDto: CreateUpdateCarbonEventDTO): ResponseEntity<CarbonEventEntity> {
        val event = carbonEventService.createCarbonEvent(createDto)
        return ResponseEntity.ok(event)
    }

    @PostMapping("/flight")
    fun createFlightCarbonEvent(
        @RequestParam passengers: Int,
        @RequestBody legs: List<FlightLeg>
    ): ResponseEntity<CarbonEventEntity> {
        val event = carbonEventService.createFlightCarbonEvent(passengers, legs)
        return ResponseEntity.ok(event)
    }

    @PostMapping("/car")
    fun createCarCarbonEvent(
        @RequestParam distanceValue: Double,
        @RequestParam vehicleModelId: String
    ): ResponseEntity<CarbonEventEntity> {
        val event = carbonEventService.createCarCarbonEvent(distanceValue, vehicleModelId)
        return ResponseEntity.ok(event)
    }

    @PostMapping("/shipping")
    fun createShippingCarbonEvent(
        @RequestParam weightValue: Double,
        @RequestParam distanceValue: Double,
        @RequestParam transportMethod: String
    ): ResponseEntity<CarbonEventEntity> {
        val event = carbonEventService.createShippingCarbonEvent(weightValue, distanceValue, transportMethod)
        return ResponseEntity.ok(event)
    }

    @PutMapping("/{id}")
    fun updateCarbonEvent(
        @PathVariable id: UUID,
        @RequestBody updateDto: CreateUpdateCarbonEventDTO
    ): ResponseEntity<CarbonEventEntity> {
        val updatedEvent = carbonEventService.updateCarbonEvent(id, updateDto)
        return ResponseEntity.ok(updatedEvent)
    }

    @DeleteMapping("/{id}")
    fun deleteCarbonEvent(@PathVariable id: UUID): ResponseEntity<Void> {
        carbonEventService.deleteCarbonEvent(id)
        return ResponseEntity.noContent().build()
    }

    @DeleteMapping
    fun deleteCarbonEventsInTimeRange(
        @RequestParam start: Date,
        @RequestParam end: Date
    ): ResponseEntity<Void> {
        carbonEventService.deleteCarbonEventsInTimeRange(start, end)
        return ResponseEntity.noContent().build()
    }
}
