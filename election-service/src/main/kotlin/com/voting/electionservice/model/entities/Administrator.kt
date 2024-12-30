package com.voting.electionservice.model.entities

import com.ethyllium.electionservice.model.entities.ElectoralCommission
import jakarta.persistence.*
import org.hibernate.annotations.UuidGenerator

@Entity
data class Administrator(
    @Id @UuidGenerator val adminId: String = "",
    val name: String = "",
    @ManyToOne(fetch = FetchType.LAZY)
    val electoralCommission: ElectoralCommission = ElectoralCommission()
)