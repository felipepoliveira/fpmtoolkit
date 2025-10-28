package io.felipepoliveira.fpmtoolkit.api.security.oauth.dto

import io.felipepoliveira.fpmtoolkit.io.felipepoliveira.fpmtoolkit.security.oauth.types.TokenRequestSpec
import org.springframework.web.bind.annotation.RequestParam

data class TokenRequest(
    @RequestParam(name = "grant_type") override val grantType: String,
    @RequestParam(name = "client_id", required = false) override val clientId: String?,
    @RequestParam(name = "code") override val code: String,
    @RequestParam(name = "code_verifier", required = false) override val codeVerifier: String?,
    @RequestParam(name = "redirect_uri") override val redirectUri: String,
    @RequestParam(name = "client_secret", required = false)override val clientSecret: String?
) : TokenRequestSpec