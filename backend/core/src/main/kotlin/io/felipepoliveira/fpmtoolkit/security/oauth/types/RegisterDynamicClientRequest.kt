package io.felipepoliveira.fpmtoolkit.io.felipepoliveira.fpmtoolkit.security.oauth.types

class RegisterDynamicClientRequest(
    /**
     * The redirect URIs registered for the client
     */
    val redirectUris: Array<String>,
    /**
     * The client name
     */
    val clientName: String,
)