package io.felipepoliveira.fpmtoolkit.api.security.oauth

import io.felipepoliveira.fpmtoolkit.BusinessRuleException
import io.felipepoliveira.fpmtoolkit.BusinessRulesError
import io.felipepoliveira.fpmtoolkit.api.security.tokens.ApiAuthenticationTokenProvider
import io.felipepoliveira.fpmtoolkit.commons.io.RandomString
import io.felipepoliveira.fpmtoolkit.features.oauth.accessToken.AccessTokenModel
import io.felipepoliveira.fpmtoolkit.features.oauth.authorizationCode.AuthorizationCodeModel
import io.felipepoliveira.fpmtoolkit.features.oauth.refreshToken.RefreshTokenCache
import io.felipepoliveira.fpmtoolkit.features.oauth.refreshToken.RefreshTokenModel
import io.felipepoliveira.fpmtoolkit.features.thirdPartyApplication.ThirdPartyApplicationDAO
import io.felipepoliveira.fpmtoolkit.features.userConsent.UserConsentDAO
import io.felipepoliveira.fpmtoolkit.features.users.UserDAO
import io.felipepoliveira.fpmtoolkit.features.users.UserModel
import io.felipepoliveira.fpmtoolkit.io.felipepoliveira.fpmtoolkit.security.oauth.types.TokenRequestSpec
import io.felipepoliveira.fpmtoolkit.security.oauth.OAuthServiceSpec
import io.felipepoliveira.fpmtoolkit.security.oauth.features.accessToken.AccessTokenDAOSpec
import io.felipepoliveira.fpmtoolkit.security.oauth.features.accessToken.AccessTokenModelSpec
import io.felipepoliveira.fpmtoolkit.security.oauth.features.authorizationCode.AuthorizationCodeDAOSpec
import io.felipepoliveira.fpmtoolkit.security.oauth.features.authorizationCode.AuthorizationCodeModelSpec
import io.felipepoliveira.fpmtoolkit.security.oauth.features.refreshToken.RefreshTokenDAOSpec
import io.felipepoliveira.fpmtoolkit.security.oauth.features.refreshToken.RefreshTokenModelSpec
import io.felipepoliveira.fpmtoolkit.security.oauth.features.userConsent.UserConsentModelSpec
import io.felipepoliveira.fpmtoolkit.security.oauth.types.ValidatedAuthorizeRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit
import java.util.UUID

@Service
class OAuthService @Autowired constructor(
    private val apiAuthenticationTokenProvider: ApiAuthenticationTokenProvider,
    private val authorizationCodeDAO: AuthorizationCodeDAOSpec,
    private val accessTokenDAOSpec: AccessTokenDAOSpec,
    private val clientDAO: ThirdPartyApplicationDAO,
    private val refreshTokenDAOSpec: RefreshTokenDAOSpec,
    private val userConsentDAO: UserConsentDAO,
    private val userDAO: UserDAO,
    private val refreshTokenCache: RefreshTokenCache,
) : OAuthServiceSpec(accessTokenDAOSpec, authorizationCodeDAO, clientDAO, refreshTokenCache, userConsentDAO) {

    override fun createAuthorizationCode(
        consent: UserConsentModelSpec,
        params: ValidatedAuthorizeRequest
    ): AuthorizationCodeModelSpec {

        val authorizationCode = AuthorizationCodeModel(
             code = RandomString.generate(RandomString.SEED_BASE62, 64),
            codeChallenge = params.codeChallenge,
            expiresAt = LocalDateTime.now().plusMinutes(2),
            codeChallengeMethod = params.codeChallengeMethod,
            redirectUri = params.redirectUri,
            requestedScopes = params.scopes,
            userId = consent.user.userId,
            clientId = consent.client.clientId
        )

        authorizationCodeDAO.persist(authorizationCode)

        return authorizationCode
    }

    override fun createAccessToken(authorizationCode: AuthorizationCodeModelSpec): AccessTokenModelSpec {
        val expiresAt = Instant.now().plus(2, ChronoUnit.MINUTES)
        val token = apiAuthenticationTokenProvider.issue(
            user = userDAO.findById(authorizationCode.userId) as UserModel? ?: throw Exception("Exception"),
            clientIdentifier = authorizationCode.clientId,
            expiresAt = expiresAt,
            roles = arrayOf(),
            organizationId = null,
            issuedAt = Instant.now()
        )
        val accessToken = AccessTokenModel(
            expiresAt = LocalDateTime.ofInstant(expiresAt, ZoneId.systemDefault()),
            token = token.token,
            issuedAt = LocalDateTime.ofInstant(token.payload.issuedAt, ZoneId.systemDefault())
        )

        if (userConsentDAO.findConsent(authorizationCode.userId, authorizationCode.clientId) == null) {
            throw BusinessRuleException(
                BusinessRulesError.INVALID_CREDENTIALS,
                "User has removed your consent"
            )
        }

        return accessToken
    }

    override fun createAccessToken(refreshToken: RefreshTokenModelSpec): AccessTokenModelSpec {
        val expiresAt = Instant.now().plus(2, ChronoUnit.MINUTES)
        val token = apiAuthenticationTokenProvider.issue(
            user = userDAO.findById(refreshToken.userId) as UserModel? ?: throw Exception("Exception"),
            clientIdentifier = refreshToken.clientId,
            expiresAt = expiresAt,
            roles = arrayOf(),
            organizationId = null,
            issuedAt = Instant.now()
        )
        val accessToken = AccessTokenModel(
            expiresAt = LocalDateTime.ofInstant(expiresAt, ZoneId.systemDefault()),
            token = token.token,
            issuedAt = LocalDateTime.ofInstant(token.payload.issuedAt, ZoneId.systemDefault())
        )

        if (userConsentDAO.findConsent(refreshToken.userId, refreshToken.clientId) == null) {
            throw BusinessRuleException(
                BusinessRulesError.INVALID_CREDENTIALS,
                "User has removed your consent"
            )
        }

        return accessToken
    }

    override fun createRefreshToken(
        params: TokenRequestSpec,
        authorizationCode: AuthorizationCodeModelSpec
    ): RefreshTokenModelSpec? {

        val clientSecret = params.clientSecret ?: return null

        val client = clientDAO.findById(authorizationCode.clientId) ?: throw BusinessRuleException(
            BusinessRulesError.INVALID_PARAMETERS,
            "Invalid 'client_id'"
        )

        if (!client.secretMatches(clientSecret)) {
            throw BusinessRuleException(
                BusinessRulesError.INVALID_PARAMETERS,
                "'client_secret' does not match"
            )
        }

        val refreshTokenId = RandomString.generate(RandomString.SEED_BASE62, 64)
        val refreshToken = RefreshTokenModel(
            token = refreshTokenId,
            expiresAt = LocalDateTime.now().plusDays(30),
            userId = authorizationCode.userId,
            clientId = authorizationCode.clientId,
            issuedAt = LocalDateTime.now()
        )

        return refreshToken

    }
}