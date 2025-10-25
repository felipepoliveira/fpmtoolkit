package io.felipepoliveira.fpmtoolkit.api.security.oauth.dto

import org.springframework.web.bind.annotation.RequestParam

data class AuthorizeRequest(
    /**
     * The client ID
     */
    @RequestParam(name = "client_id") val clientId: String,
    @RequestParam(name = "redirect_uri") val redirectUri: String,
    @RequestParam val scope: String,
    @RequestParam(required = false) val state: String?,
    @RequestParam(required = false, name = "code_challenge") val codeChallenge: String,
    @RequestParam(required = false, name = "code_challenge_method") val codeChallengeMethod: String,
)