package io.felipepoliveira.fpmtoolkit.api.security.oauth.features.userConsent

import io.felipepoliveira.fpmtoolkit.api.security.oauth.features.client.ClientModel
import io.felipepoliveira.fpmtoolkit.api.security.oauth.features.user.UserModel

interface UserConsentDAO<T: UserConsentModel> {

    /**
     * Find a user consent for a given client
     */
    fun findConsent(user: UserModel, client: ClientModel): T?

}