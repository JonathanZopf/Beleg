package org.hszg.beleg.carbon_event

import jakarta.persistence.*
import java.time.LocalDate
import java.util.Date
import java.util.UUID

/**
 * Entity class to represent a carbon event. A carbon event is an activity that causes carbon emissions.
 */
@Entity
data class CarbonEventEntity (
    /**
     * The id of the carbon event. Unique identifier.
     */
    @Id
    var id: UUID,
    /**
     * The type of the carbon event. Shows which type of activity caused the carbon emission.
     */
    var type: CarbonEventType,
    /**
     * The date of the carbon event. Shows when the carbon emission happened.
     */
    var date: LocalDate,
    /**
     * The amount of carbon emitted by the event. Measured in g.
     */
    var amount: Int,
) {
    constructor() : this(UUID.randomUUID(), CarbonEventType.FLIGHT,  LocalDate.now(), 0)
}