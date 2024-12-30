package com.voting.electionservice.model.entities

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import java.time.LocalDateTime

@Embeddable
data class NominationDetails(
    @Column(nullable = false)
    val submissionDate: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    val fee: Double = 0.0,

    @Column(nullable = false, length = 50)
    val transactionId: String = ""
)
