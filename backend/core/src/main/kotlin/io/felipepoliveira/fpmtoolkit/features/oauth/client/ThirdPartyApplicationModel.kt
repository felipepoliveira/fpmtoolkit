package io.felipepoliveira.fpmtoolkit.features.oauth.client

import io.felipepoliveira.fpmtoolkit.features.organizationMembers.OrganizationMemberRoles
import io.felipepoliveira.fpmtoolkit.security.oauth.features.client.ClientModelSpec
import jakarta.persistence.*
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

@Entity
@Table(name = "third_party_app", indexes = [
    Index(name = "UI_client_id_AT_third_party_app", columnList = "client_id", unique = true)
])
class ThirdPartyApplicationModel(
    /**
     * Third party application id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long?,

    /**
     * An unique Client ID
     */
    @Column(name = "client_id", nullable = false, length = 48)
    @field:NotBlank
    override val clientId: String,


    @Column(name = "name", nullable = false, length = 60)
    @field:NotBlank
    @field:Size(min = 1, max = 60)
    val name: String,

    /**
     * The client secret used for authentication
     */
    @Column(name = "client_secret", nullable = false, length = 64)
    @field:NotBlank
    override val clientSecret: String,

    /**
     * Store all allowed redirect URI for third party app
     */
    @field:ElementCollection(targetClass = OrganizationMemberRoles::class, fetch = FetchType.EAGER)
    @field:JoinTable(
        name = "third_party_app_allowed_redirect_uri",
        joinColumns = [JoinColumn(name = "app_id")],
    ) @Column(
        name = "redirect_uri",
        nullable = false,
        length = 255
    ) @Enumerated(EnumType.STRING)
    @field:Size(min = 1)
    override val allowedRedirectUris: List<String>,

    /**
     * Store all allowed scopes for the client
     */
    @field:ElementCollection(fetch = FetchType.EAGER)
    @field:JoinTable(
        name = "third_party_app_granted_scopes",
        joinColumns = [JoinColumn(name = "app_id")],
    ) @Column(
        name = "scope",
        nullable = false,
        length = 80
    ) @Enumerated(EnumType.STRING)
    @field:Size(min = 1)
    override val grantedScopes: List<String>

) : ClientModelSpec {

    override fun secretMatches(clientSecret: String): Boolean {
        TODO("Not yet implemented")
    }
}