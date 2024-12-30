package com.voting.electionservice.model.entities

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated

@Embeddable
data class Jurisdiction(
    @Column(name = "jurisdiction_level", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    val level: JurisdictionLevel = JurisdictionLevel.CITY,

    @Column(name = "jurisdiction_region", nullable = false, length = 100)
    val region: String = ""
)

enum class JurisdictionLevel {
    NATIONAL, STATE, DISTRICT, CITY
}