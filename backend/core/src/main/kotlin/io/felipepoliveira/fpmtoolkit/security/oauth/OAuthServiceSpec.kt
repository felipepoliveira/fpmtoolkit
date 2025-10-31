package io.felipepoliveira.fpmtoolkit.security.oauth

import io.felipepoliveira.fpmtoolkit.BusinessRuleException
import io.felipepoliveira.fpmtoolkit.BusinessRulesError
import io.felipepoliveira.fpmtoolkit.io.felipepoliveira.fpmtoolkit.security.oauth.types.TokenRequestSpec
import io.felipepoliveira.fpmtoolkit.io.felipepoliveira.fpmtoolkit.security.oauth.types.TokenResponse
import io.felipepoliveira.fpmtoolkit.security.oauth.features.accessToken.AccessTokenModelSpec
import io.felipepoliveira.fpmtoolkit.security.oauth.features.authorizationCode.AuthorizationCodeDAOSpec
import io.felipepoliveira.fpmtoolkit.security.oauth.features.authorizationCode.AuthorizationCodeModelSpec
import io.felipepoliveira.fpmtoolkit.security.oauth.features.client.ClientDAOSpec
import io.felipepoliveira.fpmtoolkit.security.oauth.features.client.ClientModelSpec
import io.felipepoliveira.fpmtoolkit.security.oauth.features.refreshToken.RefreshTokenModelSpec
import io.felipepoliveira.fpmtoolkit.security.oauth.features.user.UserModelSpec
import io.felipepoliveira.fpmtoolkit.security.oauth.features.userConsent.UserConsentDAOSpec
import io.felipepoliveira.fpmtoolkit.security.oauth.features.userConsent.UserConsentModelSpec
import io.felipepoliveira.fpmtoolkit.security.oauth.types.AuthorizeRequestSpec
import io.felipepoliveira.fpmtoolkit.security.oauth.types.AuthorizeResponseType
import io.felipepoliveira.fpmtoolkit.security.oauth.types.CodeChallengeMethod
import io.felipepoliveira.fpmtoolkit.security.oauth.types.ValidatedAuthorizeRequest
import org.springframework.beans.factory.annotation.Autowired
import java.time.Duration
import java.time.LocalDateTime

abstract class OAuthServiceSpec @Autowired constructor(
    private val authorizationCodeDAO: AuthorizationCodeDAOSpec,
    private val clientDAO: ClientDAOSpec,
    private val userConsentDAO: UserConsentDAOSpec,
) {

    /**
     * Find a ClientModel identified by its client_id
     */
    fun findClientById(clientId: String): ClientModelSpec {
        return clientDAO.findById(clientId) ?: throw BusinessRuleException(
            BusinessRulesError.INVALID_PARAMETERS,
            "Could not find client identified by id: $clientId"
        )
    }

    /**
     * Create an authorization code that is used in the /authorize pipeline
     */
    abstract fun createAuthorizationCode(consent: UserConsentModelSpec, params: ValidatedAuthorizeRequest): AuthorizationCodeModelSpec

    abstract fun createAccessToken(authorizationCode: AuthorizationCodeModelSpec): AccessTokenModelSpec

    abstract fun createRefreshToken(params: TokenRequestSpec, authorizationCode: AuthorizationCodeModelSpec): RefreshTokenModelSpec?

    fun createToken(params: TokenRequestSpec): TokenResponse {
        // fetch the authorization
        val authorizationCode = authorizationCodeDAO.findByCode(params.code) ?: throw BusinessRuleException(
            BusinessRulesError.INVALID_PARAMETERS,
            "Invalid 'code' given"
        )

        //TODO implement refrsh token logic

        // verify the 'authorization_code'
        authorizationCode.validate(params)

        val client = clientDAO.findById(params.clientId) ?: throw BusinessRuleException(
            BusinessRulesError.INVALID_PARAMETERS,
            "Invalid 'client_id': Could not find client"
        )

        if(!client.allowedRedirectUris.contains(params.redirectUri)) {
            throw BusinessRuleException(
                BusinessRulesError.INVALID_PARAMETERS,
                "Given 'redirect_uri' is not allowed for client_id ${client.clientId}"
            )
        }

        // create tokens
        val accessToken = createAccessToken(authorizationCode)
        val refreshToken = createRefreshToken(params, authorizationCode)


        val expiresInSeconds = Duration.between(LocalDateTime.now(), accessToken.expiresAt).seconds
        if (expiresInSeconds < 0) {
            throw Exception("Unexpected error: Provided access_token has an expiration date that is already expired")
        }

        // invalidate current authorization code
        authorizationCodeDAO.revoke(authorizationCode)

        return TokenResponse(
            accessToken = accessToken.token,
            tokenType = "Bearer",
            expiresIn = expiresInSeconds,
            scope = authorizationCode.requestedScopes.joinToString(" "),
            refreshToken = refreshToken?.token,
        )
    }


    /**
     * Check if a given user has given consent to the given client with the given parameters
     */
    fun tryFindConsentAndValidateAuthorizeRequest(
        user: UserModelSpec, validatedRequest: ValidatedAuthorizeRequest
    ): UserConsentModelSpec? {
        val consent = userConsentDAO.findConsent(user, validatedRequest.client)
        return if (
            consent != null && // client should have granted authorization
            consent.grants.containsAll(validatedRequest.scopes)  // consent should have all grants requested
            ) {
            consent
        } else {
            null
        }
    }

    fun validateAuthorizeRequest(authorizeRequest: AuthorizeRequestSpec): ValidatedAuthorizeRequest {

        if (authorizeRequest.responseType != "code") {
            throw BusinessRuleException(
                BusinessRulesError.INVALID_PARAMETERS,
                "'response_type' parameter should be 'code'"
            )
        }

        val client = findClientById(authorizeRequest.clientId)

        // The server can not have empty allowed redirect uri
        if (client.allowedRedirectUris.isEmpty()) {
            throw Exception(
                "An unexpected error occur in the server while fetching client data: " +
                        "client_id ${client.clientId} does not have any allowed redirect uri"
            )
        }

        // check if given redirect_uri can be used
        // fetch from request if passed, otherwise fetch the first one [0] registered on the platform
        // throw error when client has multiple allowed redirect uris
        val redirectUri = authorizeRequest.redirectUri ?:
        if (client.allowedRedirectUris.size == 1) client.allowedRedirectUris[0]
        else throw BusinessRuleException(
            BusinessRulesError.INVALID_PARAMETERS,
            "'redirect_uri' is required when client has multiple allowed redirect URIs registered"
        )
        if (!client.allowedRedirectUris.contains(redirectUri)) {
            throw BusinessRuleException(
                BusinessRulesError.INVALID_PARAMETERS,
                "Redirect uri ${authorizeRequest.redirectUri} is not allowed for client"
            )
        }

        val scopes = authorizeRequest.scope?.split(" ")
        if (scopes != null && !client.grantedScopes.containsAll(scopes)) {
            throw BusinessRuleException(
                BusinessRulesError.INVALID_PARAMETERS,
                "Scope '${authorizeRequest.scope}' is not allowed for client"
            )
        }

        val codeChallengeMethod = authorizeRequest.codeChallengeMethod
        val validatedAuthorizeRequest = ValidatedAuthorizeRequest(
            responseType = AuthorizeResponseType.CODE,
            client = client,
            codeChallenge = authorizeRequest.codeChallenge,
            codeChallengeMethod = CodeChallengeMethod.fromString(codeChallengeMethod),
            redirectUri = redirectUri,
            scopes = scopes?.toSet() ?: client.grantedScopes.toSet(),
            state = authorizeRequest.state
        )

        return validatedAuthorizeRequest
    }


}