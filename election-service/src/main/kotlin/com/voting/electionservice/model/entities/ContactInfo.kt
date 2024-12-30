package com.voting.electionservice.model.entities

import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
data class ContactInfo(
    @Column(length = 200)
    val address: String = "",

    @Column(length = 20)
    val phone: String = "",

    @Column(length = 100)
    val email: String = ""
)