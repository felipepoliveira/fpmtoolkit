package io.felipepoliveira.fpmtoolkit.api.security.oauth.dto

data class AuthorizeResponse(
    /**
     * The redirect_uri of the OAuth flow
     */
    val redirectUri: String,
    /**
     * Generated authorization code
     */
    val code: String? = null,
    /**
     * The OAuth Client id
     */
    val clientId: String? = null,
    /**
     * The code challenge
     */
    val codeChallenge: String? = null,
    /**
     * code challenge method
     */
    val codeChallengeMethod: String? = null,
    /**
     * Scope provided by OAuth request
     */
    val scope: String? = null,
    /**
     * State provided by OAuth request
     */
    val state: String? = null,
    /**
     *
     */
    val responseType: String? = null
)