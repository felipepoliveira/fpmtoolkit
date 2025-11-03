package io.felipepoliveira.fpmtoolkit.api.security.oauth.dto

import com.fasterxml.jackson.annotation.JsonProperty
import io.felipepoliveira.fpmtoolkit.io.felipepoliveira.fpmtoolkit.security.oauth.types.GeneratedToken

class TokenResponse(
    /**
     * REQUIRED. The access token issued by the authorization server.
     */
    @field:JsonProperty(value = "access_token") val accessToken: String,

    /**
     * REQUIRED. The type of the access token issued as described in Section 1.4. Value is case insensitive.
     */
    @field:JsonProperty(value = "token_type") val tokenType: String,

    /**
     * RECOMMENDED. A JSON number that represents the lifetime in seconds of the access token.
     * For example, the value 3600 denotes that the access token will expire in one hour from the time
     * the response was generated. If omitted, the authorization server SHOULD provide the lifetime via other means
     * or document the default value. Note that the authorization server may prematurely expire an access token and
     * clients MUST NOT expect an access token to be valid for the provided lifetime.
     */
    @field:JsonProperty(value = "expires_in") val expiresIn: Long,

    /**
     * RECOMMENDED, if identical to the scope requested by the client; otherwise, REQUIRED.
     * The scope of the access token as described by Section 1.4.1.
     */
    @field:JsonProperty(value = "scope") val scope: String,

    /**
     * OPTIONAL. The refresh token, which can be used to obtain new access tokens based on the grant
     * passed in the corresponding token request.
     */
    @field:JsonProperty(value = "refresh_token") val refreshToken: String?
) {
    constructor(src: GeneratedToken) : this(
        accessToken = src.accessToken,
        tokenType = src.tokenType,
        expiresIn = src.expiresIn,
        scope = src.scope,
        refreshToken = src.refreshToken
    )
}