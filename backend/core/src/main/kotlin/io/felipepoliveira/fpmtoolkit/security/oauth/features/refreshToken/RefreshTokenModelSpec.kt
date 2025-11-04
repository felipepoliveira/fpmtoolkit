package io.felipepoliveira.fpmtoolkit.security.oauth.features.refreshToken

import java.time.LocalDateTime

interface RefreshTokenModelSpec {
    /**
     * The token that will be used in the authentication process
     */
    val token: String

    /**
     * Identifier for the user
     */
    val userId: String

    /**
     * Identifier for client
     */
    val clientId: String

    /**
     * When the token was issued
     */
    val issuedAt: LocalDateTime

    /**
     * When the token will expire
     */
    val expiresAt: LocalDateTime
}