package io.felipepoliveira.fpmtoolkit.api.security.oauth.dto

import io.felipepoliveira.fpmtoolkit.security.oauth.dto.AuthorizeDTO
import org.springframework.web.bind.annotation.RequestParam

data class AuthorizeRequest(
    @RequestParam(name = "client_id") override val clientId: String,
    @RequestParam(name = "code_challenge", required = false) override val codeChallenge: String?,
    @RequestParam(name = "code_challenge_method", required = false) override val codeChallengeMethod: String?,
    @RequestParam(name = "redirect_uri", required = false) override val redirectUri: String?,
    @RequestParam(name = "scope", required = false) override val scope: String?,
    @RequestParam(name = "state", required = false) override val state: String?
) : AuthorizeDTO