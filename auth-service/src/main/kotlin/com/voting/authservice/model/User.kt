package com.voting.authservice.model

import jakarta.persistence.*
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy
import org.hibernate.envers.Audited
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.util.*
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/**
 * User entity class
 * @param id: String - unique identifier for the user
 * @param name: String - name of the user
 * @param clientId: String - unique identifier for the client and a key for the redis cache
 * @param email: String - email of the user
 * @param passwordHash: String - hashed password of the user
 * @param roles: MutableSet<String> - roles assigned to the user
 * @param createdAt: Date - date when the user was created
 * @param updatedAt: Date - date when the user was last updated
 * @param isAccountExpired: Boolean - flag to check if the account is expired
 * @param isAccountLocked: Boolean - flag to check if the account is locked
 * @param isCredentialsExpired: Boolean - flag to check if the credentials are expired
 * @param isUserEnabled: Boolean - flag to check if the user is enabled
 * @param loginSecurity: LoginSecurity - login security details of the user
 * @param refreshToken: RefreshToken - refresh token of the user
 */

@Entity
@Table(name = "users")
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Audited
data class User @OptIn(ExperimentalUuidApi::class) constructor(
    @Id @GeneratedValue(strategy = GenerationType.UUID) @Column(name = "user_id", updatable = false, nullable = false) private val id: String = "",

    @Column(unique = true, nullable = false) @NotNull(message = "Username cannot be null") @Size(
        min = 3, max = 50, message = "Username must be between 3 and 50 characters"
    ) val name: String = "",

    @Column(unique = true, nullable = false) val clientId: String = Uuid.random().toHexString(),

    @NotNull(message = "Email cannot be null") @Email(message = "Email should be valid") @Column(
        unique = true, nullable = false
    ) private val email: String = "",

    @NotNull(message = "Password cannot be null") @Size(
        min = 8, max = 100, message = "Password must be between 8 and 100 characters"
    ) @Pattern(
        regexp = "(?=.*[0-9])(?=.*[a-zA-Z]).*", message = "Password must contain at least one letter and one digit"
    ) @Column(nullable = false) val passwordHash: String = "", // Store hashed password

    @ElementCollection(fetch = FetchType.EAGER) @CollectionTable(
        name = "user_roles", joinColumns = [JoinColumn(name = "user_id")]
    ) @Column(name = "role") val roles: MutableSet<String> = mutableSetOf(), @Column(
        name = "created_at", nullable = false, updatable = false
    ) var createdAt: Date = Date(),

    @Column(name = "updated_at", nullable = false) var updatedAt: Date = Date(),

    @Column(name = "is_account_non_expired", nullable = false) var isAccountExpired: Boolean = false,

    @Column(name = "is_account_non_locked", nullable = false) var isAccountLocked: Boolean = false,

    @Column(name = "is_credentials_non_expired", nullable = false) var isCredentialsExpired: Boolean = false,

    @Column(name = "is_enabled", nullable = false) private var isUserEnabled: Boolean = true,

    @Embedded val loginSecurity: LoginSecurity = LoginSecurity(),

    @OneToOne(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true)
    var refreshToken: RefreshToken? = null
) : UserDetails {

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return roles.map { SimpleGrantedAuthority("ROLE_$it") }.toMutableSet()
    }

    override fun getPassword(): String {
        return passwordHash
    }

    override fun getUsername(): String {
        return email
    }

    override fun isAccountNonExpired(): Boolean {
        return !isAccountExpired
    }

    override fun isAccountNonLocked(): Boolean {
        return !isAccountLocked
    }

    override fun isCredentialsNonExpired(): Boolean {
        return !isCredentialsExpired
    }

    override fun isEnabled(): Boolean {
        return isUserEnabled
    }

    @PostUpdate
    fun postUpdate() {
        updatedAt = Date(System.currentTimeMillis())
    }

    @PrePersist
    fun prePersist() {
        updatedAt = Date(System.currentTimeMillis())
        createdAt = Date(System.currentTimeMillis())
    }
}