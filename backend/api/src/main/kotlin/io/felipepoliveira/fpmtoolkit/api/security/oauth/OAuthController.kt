package io.felipepoliveira.fpmtoolkit.api.security.oauth

import io.felipepoliveira.fpmtoolkit.api.controllers.BaseRestController
import io.felipepoliveira.fpmtoolkit.api.security.auth.RequestClient
import io.felipepoliveira.fpmtoolkit.api.security.oauth.dto.AuthorizeRequest
import io.felipepoliveira.fpmtoolkit.api.security.oauth.dto.TokenRequest
import io.felipepoliveira.fpmtoolkit.io.felipepoliveira.fpmtoolkit.security.oauth.types.TokenRequestSpec
import io.felipepoliveira.fpmtoolkit.security.oauth.OAuthServiceSpec
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import org.springframework.web.util.UriComponentsBuilder

@RestController
@RequestMapping("/oauth")
class OAuthController @Autowired constructor(
    private val authService: OAuthServiceSpec,
) : BaseRestController() {

    @GetMapping("/authorize")
    fun authorize(
        @ModelAttribute params: AuthorizeRequest,
        @RequestParam(required = false) prompt: String?,
        @AuthenticationPrincipal requestClient: RequestClient?): ResponseEntity<Any> {

        // check if the params (client authorization, grants and redirect uri) are valid
        val validatedRequest = authService.validateAuthorizeRequest(params)

        // if the client has a valid consent from the user app will redirect to the client callback
        val consent = if (requestClient != null && prompt != "consent") authService.tryFindConsentAndValidateAuthorizeRequest(
            requestClient, validatedRequest
        ) else null

        // if there is a valid consent redirect to the given 'redirect_uri'
        if (consent != null) {
            return redirect(
                UriComponentsBuilder
                    .fromUriString(validatedRequest.redirectUri)
                    .queryParam("code", authService.createAuthorizationCode(consent, validatedRequest).code)
                    .queryParam("state", params.state)
                    .toUriString()
            )
        }

        return redirect(
            UriComponentsBuilder
                .fromUriString("https://localhost:3000/oauth2/code/oidc-client")
                .queryParam("state", params.state)
                .queryParam("redirect_uri", params.redirectUri)
                .queryParam("code_challenge", params.codeChallenge)
                .queryParam("code_challenge_method", params.codeChallengeMethod)
                .queryParam("scope", params.scope)
                .queryParam("response_type", params.responseType)
                .toUriString()
        )
    }

    @PostMapping("/token")
    fun token(
        @ModelAttribute params: TokenRequest
    ) = ok {
        authService.createToken(params)
    }


}