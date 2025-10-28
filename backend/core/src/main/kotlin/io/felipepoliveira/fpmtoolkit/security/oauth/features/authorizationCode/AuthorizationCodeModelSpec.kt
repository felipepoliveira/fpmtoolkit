package io.felipepoliveira.fpmtoolkit.security.oauth.features.authorizationCode

import io.felipepoliveira.fpmtoolkit.BusinessRuleException
import io.felipepoliveira.fpmtoolkit.BusinessRulesError
import io.felipepoliveira.fpmtoolkit.io.felipepoliveira.fpmtoolkit.security.oauth.types.TokenRequestSpec
import io.felipepoliveira.fpmtoolkit.security.oauth.features.userConsent.UserConsentModelSpec
import io.felipepoliveira.fpmtoolkit.security.oauth.types.CodeChallengeMethod
import java.time.LocalDateTime

interface AuthorizationCodeModelSpec {

    /**
     * A unique code that identifies the authorization code
     */
    val code: String

    /**
     * The user consent data
     */
    val userConsent: UserConsentModelSpec

    /**
     * The redirect URI used in this authorization
     */
    val redirectUri: String

    /**
     * The requested scopes
     */
    val requestedScopes: Set<String>

    /**
     * The code challenge used during the authorization code issue
     */
    val codeChallenge: String?

    /**
     * The code challenge method used during the authorization code issue
     */
    val codeChallengeMethod: CodeChallengeMethod?

    /**
     * When the authorization code will expire
     */
    val expiresAt: LocalDateTime

    fun validate(refresh: TokenRequestSpec) {

        val codeChallenge = codeChallenge
        val codeChallengeMethod = codeChallengeMethod
        val codeVerifier = refresh.codeVerifier

        if (expiresAt.isBefore(LocalDateTime.now())) {
            throw BusinessRuleException(
                BusinessRulesError.INVALID_PARAMETERS,
                "Given 'code' is expired"
            )
        }

        // if there is a code challenge established validate it
        if (codeChallengeMethod != null) {

            // codeChallenge can not be null if a challenge method is defined
            if (codeChallenge == null) {
                throw Exception(
                    "An unexpected error occur: codeChallenge on AuthorizationCodeModel is null when " +
                            "codeChallengeMethod is defined as: $codeChallengeMethod"
                )
            }

            // check if codeVerifier was given
            if (codeVerifier == null) {
                throw BusinessRuleException(
                    BusinessRulesError.INVALID_PARAMETERS,
                    "code_verifier is required for the given authenication code"
                )
            }


            if (codeChallengeMethod.hash(codeVerifier) != codeChallenge) {
                throw BusinessRuleException(
                    BusinessRulesError.INVALID_PARAMETERS,
                    "The given code_verifier does not match the authentication code_challenge"
                )
            }
        }
    }
}