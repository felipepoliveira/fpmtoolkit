package io.felipepoliveira.fpmtoolkit.api.security.oauth.features.userConsent

import io.felipepoliveira.fpmtoolkit.api.security.oauth.features.client.ClientModel
import io.felipepoliveira.fpmtoolkit.api.security.oauth.features.user.UserModel
import java.time.LocalDateTime

interface UserConsentModel {
    /**
     * The user that consented
     */
    val user: UserModel

    /**
     * The client that was consented
     */
    val client: ClientModel

    /**
     * When the consent was given
     */
    val consentedAt: LocalDateTime

    /**
     * Witch grants was granted from user to the client
     */
    val grants: Set<String>

    /**
     * The code challenge used RFC 7636 — Proof Key for Code Exchange (PKCE):
     * https://datatracker.ietf.org/doc/html/rfc7636
     */
    val codeChallenge: String

    /**
     * The code challenge used RFC 7636 — Proof Key for Code Exchange (PKCE):
     * https://datatracker.ietf.org/doc/html/rfc7636
     */
    val codeChallengeMethod: String

}