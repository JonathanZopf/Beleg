package org.hszg.beleg.carbon_event

import java.util.*

/**
 * A simple DTO for creating or updating a carbon event.
 */
data class CreateUpdateCarbonEventDTO(
    /**
     * The type of the carbon event. Shows which type of activity caused the carbon emission.
     */
    val type: CarbonEventType,
    /**
     * The date of the carbon event. Shows when the carbon emission happened.
     */
    val date: Date,
    /**
     * The amount of carbon emitted by the event. Measured in g.
     */
    val amount: Int
)
