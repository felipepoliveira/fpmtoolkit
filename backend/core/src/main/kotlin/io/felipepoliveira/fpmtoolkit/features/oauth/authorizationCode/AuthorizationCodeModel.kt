package io.felipepoliveira.fpmtoolkit.features.oauth.authorizationCode

import io.felipepoliveira.fpmtoolkit.security.oauth.features.authorizationCode.AuthorizationCodeModelSpec
import io.felipepoliveira.fpmtoolkit.security.oauth.features.userConsent.UserConsentModelSpec
import io.felipepoliveira.fpmtoolkit.security.oauth.types.CodeChallengeMethod
import java.time.LocalDateTime

class AuthorizationCodeModel(
    override val code: String,
    override val userConsent: UserConsentModelSpec,
    override val redirectUri: String,
    override val requestedScopes: Set<String>,
    override val codeChallenge: String?,
    override val codeChallengeMethod: CodeChallengeMethod?,
    override val expiresAt: LocalDateTime
) : AuthorizationCodeModelSpec