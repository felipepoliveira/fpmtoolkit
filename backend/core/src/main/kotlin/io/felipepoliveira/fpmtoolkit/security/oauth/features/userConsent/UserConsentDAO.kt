package io.felipepoliveira.fpmtoolkit.security.oauth.features.userConsent

import io.felipepoliveira.fpmtoolkit.security.oauth.features.client.ClientModel
import io.felipepoliveira.fpmtoolkit.security.oauth.features.user.UserModel

interface UserConsentDAO<T: UserConsentModel> {

    /**
     * Find a user consent for a given client
     */
    fun findConsent(user: UserModel, client: ClientModel): T?

}