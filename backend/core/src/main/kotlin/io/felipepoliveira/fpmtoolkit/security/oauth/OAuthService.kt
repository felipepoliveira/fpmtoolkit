package io.felipepoliveira.fpmtoolkit.security.oauth

import io.felipepoliveira.fpmtoolkit.BusinessRuleException
import io.felipepoliveira.fpmtoolkit.BusinessRulesError
import io.felipepoliveira.fpmtoolkit.security.oauth.dto.AuthorizeDTO
import io.felipepoliveira.fpmtoolkit.security.oauth.features.authorizationCode.AuthorizationCodeDAO
import io.felipepoliveira.fpmtoolkit.security.oauth.features.authorizationCode.AuthorizationCodeModel
import io.felipepoliveira.fpmtoolkit.security.oauth.features.client.ClientDAO
import io.felipepoliveira.fpmtoolkit.security.oauth.features.client.ClientModel
import io.felipepoliveira.fpmtoolkit.security.oauth.features.user.UserDAO
import io.felipepoliveira.fpmtoolkit.security.oauth.features.user.UserModel
import io.felipepoliveira.fpmtoolkit.security.oauth.features.userConsent.UserConsentDAO
import io.felipepoliveira.fpmtoolkit.security.oauth.features.userConsent.UserConsentModel
import org.springframework.beans.factory.annotation.Autowired

abstract class OAuthService @Autowired constructor(
    private val authorizationCodeDAO: AuthorizationCodeDAO<AuthorizationCodeModel>,
    private val clientDAO: ClientDAO<ClientModel>,
    private val userDAO: UserDAO<UserModel>,
    private val userConsentDAO: UserConsentDAO<UserConsentModel>,
) {

    /**
     * Find a ClientModel identified by its client_id
     */
    fun findClientById(clientId: String): ClientModel {
        return clientDAO.findById(clientId) ?: throw BusinessRuleException(
            BusinessRulesError.INVALID_PARAMETERS,
            "Could not find client identified by id: $clientId"
        )
    }

    /**
     * Create an authorization code that is used in the /authorize pipeline
     */
    abstract fun createAuthorizationCode(consent: UserConsentModel, params: AuthorizeDTO): AuthorizationCodeModel


    /**
     * Check if a given user has given consent to the given client with the given parameters
     */
    fun tryFindConsentAndValidateAuthorizeRequest(
        user: UserModel, client: ClientModel, params: AuthorizeDTO
    ): UserConsentModel? {
        val consent = userConsentDAO.findConsent(user, client)
        return if (consent != null && // client should have granted authorization
            consent.grants.containsAll(params.scope.split(" ")) &&  // consent should have all grants requested
            client.allowedRedirectUris.contains(params.redirectUri) // redirect URI should be previously allowed
            ) {
            consent
        } else {
            null
        }
    }


}