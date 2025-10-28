package io.felipepoliveira.fpmtoolkit.security.oauth.features.refreshToken

import java.time.LocalDateTime

interface RefreshTokenModelSpec {
    /**
     * An unique identifier for refresh token
     */
    val id: String

    /**
     * The token that will be used in the authentication process
     */
    val token: String

    /**
     * When the token will expire
     */
    val expiresAt: LocalDateTime
}