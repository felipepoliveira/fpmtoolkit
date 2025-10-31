package io.felipepoliveira.fpmtoolkit.security.oauth.types

/**
 * Authorize request for /authorize
 * (https://datatracker.ietf.org/doc/html/draft-ietf-oauth-v2-1#name-authorization-request)
 */
interface AuthorizeRequestSpec {
    /**
     * REQUIRED. The authorization endpoint supports different sets of request and response parameters.
     * The client determines the type of flow by using a certain response_type value.
     * This specification defines the value code, which must be used to signal that the client wants
     * to use the authorization code flow.
     */
    val responseType: String
    /**
     * REQUIRED. The client identifier as described in Section 2.2
     */
    val clientId: String

    /**
     * REQUIRED unless the specific requirements of Section 7.5.1 are met. Code challenge derived from the code verifier.
     */
    val codeChallenge: String

    /**
     * OPTIONAL, defaults to plain if not present in the request. Code verifier transformation method is S256 or plain.
     */
    val codeChallengeMethod: String

    /**
     * OPTIONAL if only one redirect URI is registered for this client. REQUIRED if multiple redirict
     * URIs are registered for this client. See Section 2.3.2.
     */
    val redirectUri: String?

    /**
     * OPTIONAL. The scope of the access request as described by Section 1.4.1.
     */
    val scope: String?

    /**
     * OPTIONAL. An opaque value used by the client to maintain state between the request and callback.
     * The authorization server includes this value when redirecting the user agent back to the client.
     */
    val state: String?

}