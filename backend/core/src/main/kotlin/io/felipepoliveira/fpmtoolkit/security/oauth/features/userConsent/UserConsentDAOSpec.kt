package io.felipepoliveira.fpmtoolkit.security.oauth.features.userConsent

import io.felipepoliveira.fpmtoolkit.security.oauth.features.client.ClientModelSpec
import io.felipepoliveira.fpmtoolkit.security.oauth.features.user.UserModelSpec

interface UserConsentDAOSpec {

    /**
     * Find a user consent for a given client
     */
    fun findConsent(user: UserModelSpec, client: ClientModelSpec): UserConsentModelSpec?

}