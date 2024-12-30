package com.voting.electionservice.dto.request

import com.voting.electionservice.model.entities.ContactInfo
import java.time.LocalDateTime

data class PartyRequest(
    val name: String,
    val shortName: String,
    val status: String,
    val partyLeaderVoterCode: String,
    val foundingDate: Long = System.currentTimeMillis(),
    val contactInfo: ContactInfo,
    val candidates: List<String>
)
