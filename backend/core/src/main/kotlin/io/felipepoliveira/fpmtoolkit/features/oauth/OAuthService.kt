package io.felipepoliveira.fpmtoolkit.features.oauth

import io.felipepoliveira.fpmtoolkit.BusinessRuleException
import io.felipepoliveira.fpmtoolkit.BusinessRulesError
import io.felipepoliveira.fpmtoolkit.features.oauth.accessToken.AccessTokenModel
import io.felipepoliveira.fpmtoolkit.features.oauth.authorizationCode.AuthorizationCodeModel
import io.felipepoliveira.fpmtoolkit.features.oauth.client.ThirdPartyApplicationDAO
import io.felipepoliveira.fpmtoolkit.features.oauth.refreshToken.RefreshTokenModel
import io.felipepoliveira.fpmtoolkit.features.oauth.userConsent.UserConsentDAO
import io.felipepoliveira.fpmtoolkit.features.oauth.userConsent.UserConsentModel
import io.felipepoliveira.fpmtoolkit.io.felipepoliveira.fpmtoolkit.security.oauth.types.TokenRequestSpec
import io.felipepoliveira.fpmtoolkit.security.oauth.OAuthServiceSpec
import io.felipepoliveira.fpmtoolkit.security.oauth.features.accessToken.AccessTokenDAOSpec
import io.felipepoliveira.fpmtoolkit.security.oauth.features.accessToken.AccessTokenModelSpec
import io.felipepoliveira.fpmtoolkit.security.oauth.features.authorizationCode.AuthorizationCodeDAOSpec
import io.felipepoliveira.fpmtoolkit.security.oauth.features.authorizationCode.AuthorizationCodeModelSpec
import io.felipepoliveira.fpmtoolkit.security.oauth.features.client.ClientDAOSpec
import io.felipepoliveira.fpmtoolkit.security.oauth.features.refreshToken.RefreshTokenDAOSpec
import io.felipepoliveira.fpmtoolkit.security.oauth.features.refreshToken.RefreshTokenModelSpec
import io.felipepoliveira.fpmtoolkit.security.oauth.features.userConsent.UserConsentDAOSpec
import io.felipepoliveira.fpmtoolkit.security.oauth.features.userConsent.UserConsentModelSpec
import io.felipepoliveira.fpmtoolkit.security.oauth.types.AuthorizeRequestSpec
import io.felipepoliveira.fpmtoolkit.security.oauth.types.CodeChallengeMethod
import io.felipepoliveira.fpmtoolkit.security.oauth.types.ValidatedAuthorizeRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

@Service
class OAuthService @Autowired constructor(
    private val authorizationCodeDAO: AuthorizationCodeDAOSpec,
    private val accessTokenDAOSpec: AccessTokenDAOSpec,
    private val clientDAO: ThirdPartyApplicationDAO,
    private val refreshTokenDAOSpec: RefreshTokenDAOSpec,
    private val userConsentDAO: UserConsentDAO
) : OAuthServiceSpec(authorizationCodeDAO, clientDAO, userConsentDAO) {

    override fun createAuthorizationCode(
        consentReq: UserConsentModelSpec,
        params: ValidatedAuthorizeRequest
    ): AuthorizationCodeModelSpec {
        val consent = consentReq as UserConsentModel

        val authorizationCode = AuthorizationCodeModel(
            code = UUID.randomUUID().toString(),
            userConsent = consent,
            codeChallenge = params.codeChallenge,
            expiresAt = LocalDateTime.now().plusMinutes(2),
            codeChallengeMethod = if (params.codeChallenge != null)CodeChallengeMethod.fromString(params.codeChallenge) else null,
            redirectUri = params.redirectUri,
            requestedScopes = params.scopes
        )

        authorizationCodeDAO.persist(authorizationCode)

        return authorizationCode
    }

    override fun createAccessToken(authorizationCode: AuthorizationCodeModelSpec): AccessTokenModelSpec {
        val accessTokenId = UUID.randomUUID().toString()
        val accessToken = AccessTokenModel(
            id = accessTokenId,
            expiresAt = LocalDateTime.now().plusMinutes(15),
            token = accessTokenId,
            issuedAt = LocalDateTime.now()
        )

        accessTokenDAOSpec.persist(accessToken)

        return accessToken
    }

    override fun createRefreshToken(
        params: TokenRequestSpec,
        authorizationCode: AuthorizationCodeModelSpec
    ): RefreshTokenModelSpec? {
        val clientId = params.clientId
        val clientSecret = params.clientSecret
        if (clientId == null || clientSecret == null) {
            return null
        }

        val client = clientDAO.findById(clientId) ?: throw BusinessRuleException(
            BusinessRulesError.INVALID_PARAMETERS,
            "Invalid 'client_id'"
        )

        if (!client.secretMatches(clientSecret)) {
            throw BusinessRuleException(
                BusinessRulesError.INVALID_PARAMETERS,
                "'client_secret' does not match"
            )
        }

        val refreshTokenId = UUID.randomUUID().toString()
        val refreshToken = RefreshTokenModel(
            id = refreshTokenId,
            token = refreshTokenId,
            expiresAt = LocalDateTime.now().plusDays(30)
        )

        refreshTokenDAOSpec.persist(refreshToken)

        return refreshToken

    }
}