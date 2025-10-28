package io.felipepoliveira.fpmtoolkit.security.oauth.features.refreshToken

interface RefreshTokenModel {
    /**
     * An unique identifier for refresh token
     */
    val id: String

    /**
     * The token that will be used in the authentication process
     */
    val token: String
}