package io.felipepoliveira.fpmtoolkit.security.oauth

import io.felipepoliveira.fpmtoolkit.BusinessRuleException
import io.felipepoliveira.fpmtoolkit.BusinessRulesError
import io.felipepoliveira.fpmtoolkit.io.felipepoliveira.fpmtoolkit.security.oauth.types.TokenRequestSpec
import io.felipepoliveira.fpmtoolkit.io.felipepoliveira.fpmtoolkit.security.oauth.types.TokenResponse
import io.felipepoliveira.fpmtoolkit.security.oauth.features.accessToken.AccessTokenModel
import io.felipepoliveira.fpmtoolkit.security.oauth.types.AuthorizeRequestSpec
import io.felipepoliveira.fpmtoolkit.security.oauth.features.authorizationCode.AuthorizationCodeDAO
import io.felipepoliveira.fpmtoolkit.security.oauth.features.authorizationCode.AuthorizationCodeModel
import io.felipepoliveira.fpmtoolkit.security.oauth.features.client.ClientDAO
import io.felipepoliveira.fpmtoolkit.security.oauth.features.client.ClientModel
import io.felipepoliveira.fpmtoolkit.security.oauth.features.refreshToken.RefreshTokenModel
import io.felipepoliveira.fpmtoolkit.security.oauth.features.user.UserDAO
import io.felipepoliveira.fpmtoolkit.security.oauth.features.user.UserModel
import io.felipepoliveira.fpmtoolkit.security.oauth.features.userConsent.UserConsentDAO
import io.felipepoliveira.fpmtoolkit.security.oauth.features.userConsent.UserConsentModel
import io.felipepoliveira.fpmtoolkit.security.oauth.types.AuthorizeResponseType
import io.felipepoliveira.fpmtoolkit.security.oauth.types.ValidatedAuthorizeRequest
import io.felipepoliveira.fpmtoolkit.security.oauth.types.CodeChallengeMethod
import org.springframework.beans.factory.annotation.Autowired
import java.time.Duration
import java.time.LocalDateTime

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
    abstract fun createAuthorizationCode(consent: UserConsentModel, params: AuthorizeRequestSpec): AuthorizationCodeModel

    abstract fun createAccessToken(authorizationCode: AuthorizationCodeModel): AccessTokenModel

    abstract fun createRefreshToken(params: TokenRequestSpec, authorizationCode: AuthorizationCodeModel): RefreshTokenModel?

    fun createToken(params: TokenRequestSpec): TokenResponse {
        // fetch the authorization
        val authorizationCode = authorizationCodeDAO.findByCode(params.code) ?: throw BusinessRuleException(
            BusinessRulesError.INVALID_PARAMETERS,
            "Invalid 'code' given"
        )

        // verify the 'authorization_code'
        authorizationCode.validate(params)

        if(!authorizationCode.userConsent.client.allowedRedirectUris.contains(params.redirectUri)) {
            throw BusinessRuleException(
                BusinessRulesError.INVALID_PARAMETERS,
                "Given 'redirect_uri' is not allowed for client_id ${authorizationCode.userConsent.client.clientId}"
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
        user: UserModel, validatedRequest: ValidatedAuthorizeRequest
    ): UserConsentModel? {
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

        if (authorizeRequest.codeChallenge == null) {
            throw BusinessRuleException(
                BusinessRulesError.INVALID_PARAMETERS,
                "'code_challenge' parameter is required"
            )
        }

        if (authorizeRequest.codeChallenge != null && authorizeRequest.codeChallengeMethod == null) {
            throw BusinessRuleException(
                BusinessRulesError.INVALID_PARAMETERS,
                "'code_challenge_method' is required when 'code_challenge' is defined"
            )
        }

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

        return ValidatedAuthorizeRequest(
            responseType = AuthorizeResponseType.CODE,
            client = client,
            codeChallenge = authorizeRequest.codeChallenge,
            codeChallengeMethod = CodeChallengeMethod.fromAuthorizeRequest(authorizeRequest),
            redirectUri = redirectUri,
            scopes = if (scopes != null) scopes.toSet() else client.grantedScopes.toSet(),
            state = authorizeRequest.state
        )
    }


}