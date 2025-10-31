package io.felipepoliveira.fpmtoolkit.api.security.oauth

import io.felipepoliveira.fpmtoolkit.api.controllers.BaseRestController
import io.felipepoliveira.fpmtoolkit.api.security.auth.RequestClient
import io.felipepoliveira.fpmtoolkit.api.security.oauth.dto.AuthorizeRequest
import io.felipepoliveira.fpmtoolkit.api.security.oauth.dto.TokenRequest
import io.felipepoliveira.fpmtoolkit.security.oauth.OAuthServiceSpec
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import org.springframework.web.util.UriComponentsBuilder
import java.util.Optional
import kotlin.jvm.optionals.getOrNull

@RestController
@RequestMapping("/oauth")
class OAuthController @Autowired constructor(
    private val authService: OAuthServiceSpec,
) : BaseRestController() {

    @GetMapping("/authorize")
    fun authorize(
        @RequestParam(name = "client_id") clientId: String,
        @RequestParam(name = "code_challenge") codeChallenge: String,
        @RequestParam(name = "code_challenge_method") codeChallengeMethod: String,
        @RequestParam(name = "response_type") responseType: String,
        @RequestParam(name = "scope") scope: String,
        @RequestParam(name = "prompt", required = false) prompt: String?,
        @RequestParam(name = "redirect_uri", required = false) redirectUri: String?,
        @RequestParam(name = "state", required = false) state: String?,
        authentication: Authentication?): ResponseEntity<Any> {

        // check if the params (client authorization, grants and redirect uri) are valid
        val validatedRequest = authService.validateAuthorizeRequest(AuthorizeRequest(
            clientId,
            codeChallenge,
            codeChallengeMethod,
            redirectUri,
            scope,
            state,
            responseType
        ))

        // if the client has a valid consent from the user app will redirect to the client callback
        val requestClient = authentication?.principal as RequestClient?
        val consent = if (requestClient != null && prompt != "consent") authService.tryFindConsentAndValidateAuthorizeRequest(
            requestClient, validatedRequest
        ) else null

        // if there is a valid consent redirect to the given 'redirect_uri'
        if (consent != null) {
            return redirect(
                UriComponentsBuilder
                    .fromUriString(validatedRequest.redirectUri)
                    .queryParam("code", authService.createAuthorizationCode(consent, validatedRequest).code)
                    .queryParam("state", state)
                    .toUriString()
            )
        }

        return redirect(
            UriComponentsBuilder
                .fromUriString("https://localhost:3000/oauth2/code/oidc-client")
                .queryParam("state", state)
                .queryParam("redirect_uri", redirectUri)
                .queryParam("code_challenge", codeChallenge)
                .queryParam("code_challenge_method", codeChallengeMethod)
                .queryParam("scope", scope)
                .queryParam("response_type", responseType)
                .toUriString()
        )
    }

    @PostMapping("/token")
    fun token(
        @RequestParam(name = "grant_type") grantType: String,
        @RequestParam(name = "client_id") clientId: String,
        @RequestParam(name = "code") code: String,
        @RequestParam(name = "code_verifier", required = false) codeVerifier: String?,
        @RequestParam(name = "redirect_uri") redirectUri: String,
        @RequestParam(name = "client_secret", required = false) clientSecret: String?
    ) = ok {
        authService.createToken(TokenRequest(
            grantType = grantType,
            clientId = clientId,
            code = code,
            codeVerifier = codeVerifier,
            redirectUri = redirectUri,
            clientSecret = clientSecret
        ))
    }


}