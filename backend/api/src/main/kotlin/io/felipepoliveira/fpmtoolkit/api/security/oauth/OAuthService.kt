package io.felipepoliveira.fpmtoolkit.api.security.oauth

import io.felipepoliveira.fpmtoolkit.BusinessRuleException
import io.felipepoliveira.fpmtoolkit.BusinessRulesError
import io.felipepoliveira.fpmtoolkit.api.security.oauth.dto.AuthorizeRequest
import io.felipepoliveira.fpmtoolkit.io.felipepoliveira.fpmtoolkit.security.oauth.dto.AuthorizeRequest
import io.felipepoliveira.fpmtoolkit.api.security.oauth.features.client.ClientDAO
import io.felipepoliveira.fpmtoolkit.api.security.oauth.features.client.ClientModel
import io.felipepoliveira.fpmtoolkit.api.security.oauth.features.user.UserDAO
import io.felipepoliveira.fpmtoolkit.api.security.oauth.features.user.UserModel
import io.felipepoliveira.fpmtoolkit.api.security.oauth.features.userConsent.UserConsentDAO
import io.felipepoliveira.fpmtoolkit.api.security.oauth.features.userConsent.UserConsentModel
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class OAuthService @Autowired constructor(
    private val clientDAO: ClientDAO<ClientModel>,
    private val userDAO: UserDAO<UserModel>,
    private val userConsentDAO: UserConsentDAO<UserConsentModel>,
) {

    fun findClientById(clientId: String): ClientModel {
        return clientDAO.findById(clientId) ?: throw BusinessRuleException(
            BusinessRulesError.INVALID_PARAMETERS,
            "Could not find client identified by id: $clientId"
        )
    }

    fun tryFindConsentAndValidate(
        user: UserModel, client: ClientModel, params: AuthorizeRequest
    ): UserConsentModel? {
        val consent = userConsentDAO.findConsent(user, client)
        return if (consent != null && consent.grants.containsAll(params.scope.split(" "))) {
            consent
        } else {
            null
        }
    }


}