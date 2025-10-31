package io.felipepoliveira.fpmtoolkit.api.security.oauth.dto

import io.felipepoliveira.fpmtoolkit.security.oauth.types.AuthorizeRequestSpec
import org.springframework.web.bind.annotation.RequestParam

data class AuthorizeRequest(
    override val clientId: String,
    override val codeChallenge: String,
    override val codeChallengeMethod: String,
    override val redirectUri: String?,
    override val scope: String,
    override val state: String?,
    override val responseType: String
) : AuthorizeRequestSpec