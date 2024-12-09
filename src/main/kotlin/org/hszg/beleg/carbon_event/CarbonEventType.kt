package org.hszg.beleg.carbon_event

/**
 * Enum class to represent the different types of carbon events.
 * Shows which type of activity caused the carbon emission, so that the user can see where the carbon footprint comes from.
 * Is determined by the rest endpoint used for getting a calculation.
 */
enum class CarbonEventType {
    FLIGHT, SHIPPING, CAR
}