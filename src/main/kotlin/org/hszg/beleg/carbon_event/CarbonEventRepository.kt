package org.hszg.beleg.carbon_event

import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface CarbonEventRepository : JpaRepository<CarbonEventEntity, UUID> {
    fun findByDateIsBetween(start: Date, end: Date): List<CarbonEventEntity>

    fun findByTypeAndDateIsBetween(type: CarbonEventType, start: Date, end: Date): List<CarbonEventEntity>
}