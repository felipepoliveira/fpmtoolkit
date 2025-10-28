package io.felipepoliveira.fpmtoolkit.security.oauth.features.userConsent

import io.felipepoliveira.fpmtoolkit.security.oauth.features.client.ClientModelSpec
import io.felipepoliveira.fpmtoolkit.security.oauth.features.user.UserModelSpec
import java.time.LocalDateTime

interface UserConsentModelSpec {
    /**
     * The user that consented
     */
    val user: UserModelSpec

    /**
     * The client that was consented
     */
    val client: ClientModelSpec

    /**
     * When the consent was given
     */
    val consentedAt: LocalDateTime

    /**
     * Witch grants was granted from user to the client
     */
    val grants: Set<String>
}