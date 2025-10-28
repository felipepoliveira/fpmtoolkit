package io.felipepoliveira.fpmtoolkit.security.oauth.features.accessToken

import java.time.LocalDateTime

interface AccessTokenModel {
    /**
     * A unique identifier for access token
     */
    val id: String
    /**
     * Represents the token that will be used as the authentication method
     */
    val token: String

    /**
     * When the token was issued
     */
    val issuedAt: LocalDateTime

    /**
     * When the token will expire
     */
    val expiresAt: LocalDateTime
}