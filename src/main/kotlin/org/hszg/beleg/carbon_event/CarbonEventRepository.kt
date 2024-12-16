package org.hszg.beleg.carbon_event

import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDate
import java.util.*

interface CarbonEventRepository : JpaRepository<CarbonEventEntity, UUID> {
    fun findByDateIsBetween(start: LocalDate, end: LocalDate): List<CarbonEventEntity>

    fun findByTypeAndDateIsBetween(type: CarbonEventType, start: LocalDate, end: LocalDate): List<CarbonEventEntity>
}