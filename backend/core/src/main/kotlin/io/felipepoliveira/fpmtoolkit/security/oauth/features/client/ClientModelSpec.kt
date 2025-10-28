package io.felipepoliveira.fpmtoolkit.security.oauth.features.client

interface ClientModelSpec {
    /**
     * An unique ID to identify the client
     */
    val clientId: String

    /**
     * The client secret used for Client Authentication process
     * (https://datatracker.ietf.org/doc/html/draft-ietf-oauth-v2-1#name-client-authentication)
     */
    val clientSecret: String

    /**
     * Store the allowed URIs for the client
     */
    val allowedRedirectUris: List<String>

    /**
     * Store granted roles for the given client
     */
    val grantedScopes: List<String>

    /**
     * Return a flag indicating if the given secret matches
     */
    fun secretMatches(clientSecret: String): Boolean
}