package io.felipepoliveira.fpmtoolkit.security.oauth

import io.felipepoliveira.fpmtoolkit.BusinessRuleException
import io.felipepoliveira.fpmtoolkit.BusinessRulesError
import io.felipepoliveira.fpmtoolkit.io.felipepoliveira.fpmtoolkit.security.oauth.features.dynamicClient.DynamicClientDAOSpec
import io.felipepoliveira.fpmtoolkit.io.felipepoliveira.fpmtoolkit.security.oauth.features.dynamicClient.DynamicClientModelSpec
import io.felipepoliveira.fpmtoolkit.io.felipepoliveira.fpmtoolkit.security.oauth.types.TokenRequestSpec
import io.felipepoliveira.fpmtoolkit.io.felipepoliveira.fpmtoolkit.security.oauth.types.GeneratedToken
import io.felipepoliveira.fpmtoolkit.io.felipepoliveira.fpmtoolkit.security.oauth.types.RegisterDynamicClientRequest
import io.felipepoliveira.fpmtoolkit.security.oauth.features.accessToken.AccessTokenDAOSpec
import io.felipepoliveira.fpmtoolkit.security.oauth.features.accessToken.AccessTokenModelSpec
import io.felipepoliveira.fpmtoolkit.security.oauth.features.authorizationCode.AuthorizationCodeDAOSpec
import io.felipepoliveira.fpmtoolkit.security.oauth.features.authorizationCode.AuthorizationCodeModelSpec
import io.felipepoliveira.fpmtoolkit.security.oauth.features.client.ClientDAOSpec
import io.felipepoliveira.fpmtoolkit.security.oauth.features.client.ClientModelSpec
import io.felipepoliveira.fpmtoolkit.security.oauth.features.refreshToken.RefreshTokenDAOSpec
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
    private val accessTokenDAO: AccessTokenDAOSpec,
    private val authorizationCodeDAO: AuthorizationCodeDAOSpec,
    private val clientDAO: ClientDAOSpec,
    private val dynamicClientDAO: DynamicClientDAOSpec,
    private val refreshTokenDAO: RefreshTokenDAOSpec,
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

    abstract fun createAccessToken(refreshToken: RefreshTokenModelSpec): AccessTokenModelSpec

    abstract fun createDynamicClient(reqData: RegisterDynamicClientRequest): DynamicClientModelSpec

    abstract fun createRefreshToken(params: TokenRequestSpec, authorizationCode: AuthorizationCodeModelSpec): RefreshTokenModelSpec?

    private fun createTokenFromAuthorizeCodeGrant(params: TokenRequestSpec): GeneratedToken {
        val code = params.code ?: throw BusinessRuleException(
            BusinessRulesError.INVALID_PARAMETERS,
            "'code' is required for grant ${params.grantType}"
        )

        // fetch the authorization code
        val authorizationCode = authorizationCodeDAO.findByCode(code) ?: throw BusinessRuleException(
            BusinessRulesError.INVALID_PARAMETERS,
            "Invalid 'code' given"
        )

        // verify the 'authorization_code'
        authorizationCode.validate(params)

        val client = clientDAO.findById(authorizationCode.clientId) ?: throw BusinessRuleException(
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

        accessTokenDAO.persist(accessToken)
        if(refreshToken != null ) refreshTokenDAO.persist(refreshToken)


        val expiresInSeconds = Duration.between(LocalDateTime.now(), accessToken.expiresAt).seconds
        if (expiresInSeconds < 0) {
            throw Exception("Unexpected error: Provided access_token has an expiration date that is already expired")
        }

        // invalidate current authorization code
        authorizationCodeDAO.revoke(authorizationCode)

        return GeneratedToken(
            accessToken = accessToken.token,
            tokenType = "Bearer",
            expiresIn = expiresInSeconds,
            scope = authorizationCode.requestedScopes.joinToString(" "),
            refreshToken = refreshToken?.token,
        )
    }

    private fun createTokenFromRefreshTokenGrant(params: TokenRequestSpec): GeneratedToken {
        val refreshTokenFromReq = params.refreshToken ?: throw BusinessRuleException(
            BusinessRulesError.INVALID_PARAMETERS,
            "'refresh_token' is required on grant ${params.grantType}"
        )

        val refreshTokenPayload = refreshTokenDAO.findByToken(refreshTokenFromReq) ?: throw BusinessRuleException(
            BusinessRulesError.INVALID_PARAMETERS,
            "invalid 'refresh_token' given"
        )

        val consent = userConsentDAO.findConsent(refreshTokenPayload.userId, refreshTokenPayload.clientId) ?: throw BusinessRuleException(
            BusinessRulesError.INVALID_PARAMETERS,
            "user has removed consent for client"
        )

        val accessToken = createAccessToken(refreshTokenPayload)
        accessTokenDAO.persist(accessToken)

        val expiresInSeconds = Duration.between(LocalDateTime.now(), accessToken.expiresAt).seconds
        if (expiresInSeconds < 0) {
            throw Exception("Unexpected error: Provided access_token has an expiration date that is already expired")
        }

        return GeneratedToken(
            accessToken = accessToken.token,
            tokenType = "Bearer",
            expiresIn = expiresInSeconds,
            scope = consent.grants.joinToString(" "),
            refreshToken = refreshTokenPayload.token,
        )
    }

    fun createToken(params: TokenRequestSpec): GeneratedToken {
        return when (params.grantType) {
            "authorization_code" -> {
                createTokenFromAuthorizeCodeGrant(params)
            }
            "refresh_token" -> {
                createTokenFromRefreshTokenGrant(params)
            }
            else -> {
                throw BusinessRuleException(
                    BusinessRulesError.INVALID_PARAMETERS,
                    "invalid 'grant_type' given"
                )
            }
        }
    }

    fun registerDynamicClient(reqData: RegisterDynamicClientRequest): DynamicClientModelSpec {

        // evaluate redirect_uri validity
        if (reqData.redirectUris.isEmpty()) {
            throw BusinessRuleException(
                BusinessRulesError.INVALID_PARAMETERS,
                "redirect_uris can not be null or empty"
            )
        }

        val client = createDynamicClient(reqData)
        dynamicClientDAO.persist(client)

        return client
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