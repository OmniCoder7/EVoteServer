package com.voting.electionservice.model.entities

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import java.time.LocalDateTime

@Embeddable
data class PersonalInfo(
    @Column(nullable = false, length = 100)
    val fullName: String = "",

    @Column(nullable = false)
    val dateOfBirth: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false, length = 20)
    val gender: String = ""
)