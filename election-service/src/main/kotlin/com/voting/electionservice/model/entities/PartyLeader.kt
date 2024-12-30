package com.voting.electionservice.model.entities

import jakarta.persistence.*
import org.hibernate.annotations.UuidGenerator
import java.time.LocalDateTime

@Entity
data class PartyLeader(
    @Id @UuidGenerator
    val partyLeaderId: String = "",
    @OneToOne(fetch = FetchType.LAZY)
    val party: Party = Party(),
    val name: String = "",
    val dateOfBirth: LocalDateTime = LocalDateTime.now()
)
