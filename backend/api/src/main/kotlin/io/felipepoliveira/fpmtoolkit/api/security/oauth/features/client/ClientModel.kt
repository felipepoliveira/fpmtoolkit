package io.felipepoliveira.fpmtoolkit.api.security.oauth.features.client

interface ClientModel {
    /**
     * An unique ID to identify the client
     */
    val clientId: String

    /**
     * Store the allowed URIs for the client
     */
    val allowedRedirectUris: Set<String>

    /**
     * Store granted roles for the given client
     */
    val grantedScopes: Set<String>
}