package io.felipepoliveira.fpmtoolkit.api.security.oauth

import io.felipepoliveira.fpmtoolkit.BusinessRuleException
import io.felipepoliveira.fpmtoolkit.BusinessRulesError
import io.felipepoliveira.fpmtoolkit.api.controllers.BaseRestController
import io.felipepoliveira.fpmtoolkit.api.security.auth.RequestClient
import io.felipepoliveira.fpmtoolkit.api.security.oauth.dto.AuthorizeRequest
import io.felipepoliveira.fpmtoolkit.security.oauth.OAuthService
import io.felipepoliveira.fpmtoolkit.security.oauth.features.client.ClientModel
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.util.UriComponentsBuilder

@RestController
@RequestMapping("/oauth")
class OAuthController @Autowired constructor(
    private val authService: OAuthService,
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
                    .queryParam("code", authService.createAuthorizationCode(consent, params).code)
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


}