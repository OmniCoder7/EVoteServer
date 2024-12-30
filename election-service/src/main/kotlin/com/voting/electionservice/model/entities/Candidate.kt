package com.voting.electionservice.model.entities

import com.voting.electionservice.model.type.CandidateStatus
import jakarta.persistence.*
import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy
import org.hibernate.annotations.UuidGenerator
import org.hibernate.envers.Audited

@Entity
@Audited
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
data class Candidate(

    @Id @UuidGenerator val candidateId: String = "",

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    val election: Election = Election(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "party_id", nullable = false)
    val party: Party = Party(),

    @Embedded
    val personalInfo: PersonalInfo = PersonalInfo(),

    @Embedded
    val contactInfo: ContactInfo = ContactInfo(),

    @Column(nullable = false, length = 100)
    val constituency: String = "",

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    var status: CandidateStatus = CandidateStatus.APPROVED,

    @Embedded
    val nominationDetails: NominationDetails = NominationDetails(),
    val qualifications: String = ""
)