package com.voting.electionservice.model.entities

import jakarta.persistence.*
import org.hibernate.annotations.ColumnTransformer
import org.hibernate.annotations.UuidGenerator
import org.hibernate.envers.Audited
import java.time.LocalDateTime

@Entity
@Table(
    name = "votes",
    indexes = [
        Index(name = "idx_vote_election", columnList = "election_id"),
        Index(name = "idx_vote_booth", columnList = "booth_id"),
        Index(name = "idx_vote_timestamp", columnList = "timestamp")
    ]
)
@Audited
data class Voter(
    @Id @UuidGenerator val voterId: String = "",
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "election_id", nullable = false)
    val election: Election = Election(),

    @Column(nullable = false)
    @ColumnTransformer(
        read = "pgp_sym_decrypt(voter_id::bytea, current_setting('app.encryption_key'))",
        write = "pgp_sym_encrypt(?, current_setting('app.encryption_key'))"
    )
    val voterCard: String = "",

    @Column(nullable = false)
    @ColumnTransformer(
        read = "pgp_sym_decrypt(candidate_id::bytea, current_setting('app.encryption_key'))",
        write = "pgp_sym_encrypt(?, current_setting('app.encryption_key'))"
    )
    val candidateId: String = "",

    @Column(nullable = false)
    val timestamp: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false, length = 50)
    val boothId: String = "",

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    var status: VoterStatus = VoterStatus.ACTIVE,

    @Column(nullable = false)
    val hash: String = "",

    @ManyToOne
    val electoralCommission: ElectoralCommission = ElectoralCommission()
)