package com.voting.electionservice.dto.request

import com.voting.electionservice.model.entities.ContactInfo
import com.voting.electionservice.model.entities.Jurisdiction

data class ElectoralCommissionRequest(
    val name: String,
    val code: String,
    val type: String,
    val status: String,
    val jurisdiction: Jurisdiction,
    val contactInfo: ContactInfo,
    val administrators: List<String>,
    val voters: List<String>
)
