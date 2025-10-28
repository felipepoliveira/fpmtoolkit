package io.felipepoliveira.fpmtoolkit.security.oauth.features.userConsent

import io.felipepoliveira.fpmtoolkit.security.oauth.features.client.ClientModel
import io.felipepoliveira.fpmtoolkit.security.oauth.features.user.UserModel
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
}