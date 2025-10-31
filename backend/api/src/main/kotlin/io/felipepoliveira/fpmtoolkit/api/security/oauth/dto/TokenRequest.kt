package io.felipepoliveira.fpmtoolkit.api.security.oauth.dto

import io.felipepoliveira.fpmtoolkit.io.felipepoliveira.fpmtoolkit.security.oauth.types.TokenRequestSpec
import org.springframework.web.bind.annotation.RequestParam

data class TokenRequest(
    override val grantType: String,
    override val clientId: String,
    override val code: String,
    override val codeVerifier: String?,
    override val redirectUri: String,
    override val clientSecret: String?
) : TokenRequestSpec