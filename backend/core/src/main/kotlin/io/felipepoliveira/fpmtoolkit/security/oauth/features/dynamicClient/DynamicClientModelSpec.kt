package io.felipepoliveira.fpmtoolkit.io.felipepoliveira.fpmtoolkit.security.oauth.features.dynamicClient

import io.felipepoliveira.fpmtoolkit.security.oauth.features.client.ClientModelSpec
import java.time.Instant

interface DynamicClientModelSpec {
    /**
     * The generated client_id
     */
    val clientId: String

    /**
     * Client name identifier
     */
    val clientName: String

    /**
     * The allowed redirect_uri associated with the client
     */
    val allowedRedirectUris: List<String>

    /**
     The * granted_scopes associated with the client
     */
    val grantedScopes: List<String>

    /**
     * The generated client_secret.
     */
    val clientSecret: String

    /**
     * When the client_id was issued
     */
    val clientIdIssuedAt: Instant?

    /**
     * When the client_secret will expire
     */
    val clientSecretExpiresAt: Instant?

    /**
     * Create an instance of ClientModelSpec based on the data of this dynamic client model instance
     */
    fun toClient(): ClientModelSpec
}