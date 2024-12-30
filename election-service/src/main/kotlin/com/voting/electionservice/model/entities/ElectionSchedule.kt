package com.voting.electionservice.model.entities

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import java.time.LocalDateTime

@Embeddable
data class ElectionSchedule(
    @Column(nullable = false)
    val nominationStart: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    val nominationEnd: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    val votingStart: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    val votingEnd: LocalDateTime = LocalDateTime.now()
)