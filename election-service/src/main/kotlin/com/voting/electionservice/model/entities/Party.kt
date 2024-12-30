package com.voting.electionservice.model.entities

import com.voting.electionservice.model.type.PartyStatus
import jakarta.persistence.*
import org.hibernate.annotations.BatchSize
import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy
import org.hibernate.annotations.UuidGenerator
import org.hibernate.envers.Audited
import java.time.LocalDateTime

@Entity
@Table(
    name = "parties",
    indexes = [Index(
        name = "idx_party_registration",
        columnList = "registrationNumber",
        unique = true
    ), Index(name = "idx_party_status", columnList = "status"), Index(name = "idx_party_name", columnList = "name")]
)
@Audited
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
data class Party(
    @Id @UuidGenerator val partyId: String = "",

    @Column(nullable = false, length = 100) val name: String = "",

    @Column(nullable = false, length = 20) val shortName: String = "",

    @Column(nullable = false, unique = true, length = 50) val registrationNumber: String = "",

    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20) var status: PartyStatus = PartyStatus.ACTIVE,

    @Column(nullable = false) val foundingDate: LocalDateTime = LocalDateTime.now(),

    @Embedded val contactInfo: ContactInfo = ContactInfo(),

    @OneToOne(
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
        mappedBy = "party"
    ) @JoinColumn(name = "party_id") @BatchSize(size = 20) val leadership: PartyLeader = PartyLeader(),

    @OneToMany(
        mappedBy = "party",
        cascade = [CascadeType.ALL]
    ) @BatchSize(size = 20) val candidates: List<Candidate> = mutableListOf()
)