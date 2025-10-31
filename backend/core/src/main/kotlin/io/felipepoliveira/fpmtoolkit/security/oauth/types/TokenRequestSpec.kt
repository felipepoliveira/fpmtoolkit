package io.felipepoliveira.fpmtoolkit.io.felipepoliveira.fpmtoolkit.security.oauth.types

interface TokenRequestSpec {
    /**
     * REQUIRED. Identifier of the grant type the client uses with the particular token request.
     * This specification defines the values authorization_code, refresh_token, and client_credentials.
     * The grant type determines the further parameters required or supported by the token request.
     * The details of those grant types are defined below.
     */
    val grantType: String

    /**
     * OPTIONAL. The client identifier is needed when a form of client authentication that relies on the parameter is
     * used, or the grant_type requires identification of public clients.
     */
    val clientId: String

    /**
     * REQUIRED. The client secret
     */
    val clientSecret: String?

    /**
     * REQUIRED. The authorization code received from the authorization server.
     */
    val code: String

    /**
     * REQUIRED, if the code_challenge parameter was included in the authorization request.
     * MUST NOT be used otherwise. The original code verifier string.
     */
    val codeVerifier: String?

    /**
     *
     */
    val redirectUri: String
}