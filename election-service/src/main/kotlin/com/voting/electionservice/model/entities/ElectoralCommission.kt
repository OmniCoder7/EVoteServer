package com.voting.electionservice.model.entities

import com.voting.electionservice.model.type.CommissionStatus
import com.voting.electionservice.model.type.CommissionType
import jakarta.persistence.*
import org.hibernate.annotations.BatchSize
import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy
import org.hibernate.annotations.UuidGenerator
import org.hibernate.envers.Audited

@Entity
@Table(
    name = "electoral_commissions", indexes = [Index(
        name = "idx_commission_code", columnList = "code", unique = true
    ), Index(name = "idx_commission_status", columnList = "status"), Index(
        name = "idx_commission_jurisdiction", columnList = "jurisdiction_level,jurisdiction_region"
    )]
)
@Audited
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
data class ElectoralCommission(

    @Id @UuidGenerator val electoralCommissionId: String = "",

    @Column(nullable = false, length = 100) val name: String = "",

    @Column(nullable = false, unique = true, length = 20) val code: String = "",

    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20) val type: CommissionType = CommissionType.LOCAL,

    @Enumerated(EnumType.STRING) @Column(
        nullable = false, length = 20
    ) var status: CommissionStatus = CommissionStatus.ACTIVE,

    @Embedded val jurisdiction: Jurisdiction = Jurisdiction(),

    @Embedded val contactInfo: ContactInfo = ContactInfo(),

    @OneToMany(
        cascade = [CascadeType.ALL], orphanRemoval = true, mappedBy = "electoralCommission"
    ) @JoinColumn(name = "commission_id") @BatchSize(size = 20) val administrators: MutableList<Administrator> = mutableListOf(),

    @OneToMany(mappedBy = "commission") @BatchSize(size = 20) val elections: MutableList<Election> = mutableListOf(),

    @OneToMany(mappedBy = "electoralCommission") @BatchSize(size = 20) val voters: MutableList<Voter> = mutableListOf()
)