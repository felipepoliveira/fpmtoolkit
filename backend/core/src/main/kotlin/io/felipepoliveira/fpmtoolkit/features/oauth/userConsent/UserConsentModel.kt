package io.felipepoliveira.fpmtoolkit.features.oauth.userConsent

import com.fasterxml.jackson.annotation.JsonIgnore
import io.felipepoliveira.fpmtoolkit.features.oauth.client.ThirdPartyApplicationModel
import io.felipepoliveira.fpmtoolkit.features.organizationMembers.OrganizationMemberRoles
import io.felipepoliveira.fpmtoolkit.features.users.UserModel
import io.felipepoliveira.fpmtoolkit.security.oauth.features.userConsent.UserConsentModelSpec
import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.time.LocalDateTime

@Entity
@Table(name = "user_consent_to_client", indexes = [
    Index(name = "UI_user_id_AND_client_id_AT_user_consent_to_third_party_app", columnList = "user_id, client_id", unique = true)
])
class UserConsentModel(
    /**
     * Unique ID for user consent
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long?,

    /**
     * The user that made the consent
     */
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @field:NotNull
    override val user: UserModel,

    /**
     * The client that received the consent
     */
    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    @field:NotNull
    override val client: ThirdPartyApplicationModel,

    /**
     * When the user has consented
     */
    @Column(name = "consented_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    override val consentedAt: LocalDateTime,

    /**
     * Store all allowed redirect URI for third party app
     */
    @field:ElementCollection(fetch = FetchType.EAGER)
    @field:JoinTable(
        name = "user_consent_allowed_grants",
        joinColumns = [JoinColumn(name = "consent_id")],
    ) @Column(
        name = "role",
        nullable = false,
        length = 255
    ) @Enumerated(EnumType.STRING)
    @field:Size(min = 1)
    override val grants: Set<String>
) : UserConsentModelSpec