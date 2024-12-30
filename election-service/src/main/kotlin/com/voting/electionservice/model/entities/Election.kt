package com.voting.electionservice.model.entities

import com.voting.electionservice.model.type.ElectionStatus
import com.voting.electionservice.model.type.ElectionType
import jakarta.persistence.*
import org.hibernate.annotations.BatchSize
import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy
import org.hibernate.annotations.UuidGenerator
import org.hibernate.envers.Audited

@Entity
@Table(
    name = "elections",
    indexes = [Index(name = "idx_election_type_status", columnList = "type,status"), Index(
        name = "idx_election_dates",
        columnList = "votingStart,votingEnd"
    ), Index(name = "idx_election_commission", columnList = "commission_id")]
)
@Audited
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
data class Election(
    @Id @UuidGenerator val electionId: String = "",

    @Column(nullable = false, length = 200) val title: String = "",

    @Enumerated(EnumType.STRING) @Column(
        nullable = false,
        length = 20
    ) val type: ElectionType = ElectionType.BY_ELECTION,

    @Enumerated(EnumType.STRING) @Column(
        nullable = false,
        length = 20
    ) var status: ElectionStatus = ElectionStatus.SCHEDULED,

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(
        name = "commission_id",
        nullable = false
    ) val commission: ElectoralCommission = ElectoralCommission(),

    @Embedded val schedule: ElectionSchedule = ElectionSchedule(), val constituencyName: String = "",

    @OneToMany(mappedBy = "election") @BatchSize(size = 50) val candidates: List<Candidate> = mutableListOf(),

    @OneToMany(mappedBy = "election") @BatchSize(size = 100) val voters: MutableList<Voter> = mutableListOf()
)