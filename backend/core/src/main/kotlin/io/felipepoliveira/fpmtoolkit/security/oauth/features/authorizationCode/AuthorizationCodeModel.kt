package io.felipepoliveira.fpmtoolkit.security.oauth.features.authorizationCode

import io.felipepoliveira.fpmtoolkit.security.oauth.features.userConsent.UserConsentModel
import java.time.LocalDateTime

interface AuthorizationCodeModel {

    /**
     * A unique code that identifies the authorization code
     */
    val code: String

    /**
     * The user consent data
     */
    val userConsent: UserConsentModel

    /**
     * The redirect URI used in this authorization
     */
    val redirectUri: String

    /**
     * The requested scopes
     */
    val requestedScopes: Set<String>

    /**
     * The code challenge used during the authorization code issue
     */
    val codeChallenge: String

    /**
     * The code challenge method used during the authorization code issue
     */
    val codeChallengeMethod: String

    /**
     * When the authorization code will expire
     */
    val expiresAt: LocalDateTime
}