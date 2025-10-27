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
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.util.UriComponentsBuilder

@RestController
@RequestMapping("/oauth")
class OAuthController @Autowired constructor(
    private val authService: OAuthService,
) : BaseRestController() {

    @GetMapping("/authorize")
    fun authorize(@ModelAttribute params: AuthorizeRequest, @AuthenticationPrincipal requestClient: RequestClient?): ResponseEntity<Any> {
        val grants = params.scope.split(" ").toSet()

        // check if the params (client authorization, grants and redirect uri) are valid
        val client = validateAuthorizeRequest(params)


        // if the client has a valid consent from the user app will redirect to the client callback
        val consent = if (requestClient != null) authService.tryFindConsentAndValidateAuthorizeRequest(
            requestClient, client, params
        ) else null

        // if there is a valid consent redirect to the given 'redirect_uri'
        if (consent != null) {

            if (client.allowedRedirectUris.size != 1 && params.redirectUri == null) {

            }
            val redirectUri = if (params.redirectUri != null) params.redirectUri else client.allowedRedirectUris[0]

            return redirect(
                UriComponentsBuilder
                    .fromUriString(params.redirectUri)
                    .queryParam("code", authService.createAuthorizationCode(consent, params).code)
                    .queryParam("state", params.state)
                    .toUriString()
            )
        }

        return redirect(
            UriComponentsBuilder
                .fromUriString("https://localhost:3000/oauth2/code/oidc-client")
                .queryParam("redirect_uri", params.redirectUri)
                .queryParam("scope", params.scope)
                .queryParam("client_id", params.clientId)
                .queryParam("state", params.state)
                .queryParam("code_challenge", params.codeChallenge)
                .queryParam("code_challenge_method", params.codeChallengeMethod)
                .toUriString()
        )
    }

    private fun validateAuthorizeRequest(authorizeRequest: AuthorizeRequest): ClientModel {
        val client = authService.findClientById(authorizeRequest.clientId)

        //
        if (client.allowedRedirectUris.isEmpty()) {
            throw Exception(
                "An unexpected error occur in the server while fetching client data: " +
                    "client_id ${client.clientId} does not have any allowed redirect uri"
            )
        }

        // check if given redirect_uri can be used
        if (!client.allowedRedirectUris.contains(authorizeRequest.redirectUri)) {
            throw BusinessRuleException(
                BusinessRulesError.INVALID_PARAMETERS,
                "Redirect uri ${authorizeRequest.redirectUri} is not allowed for client"
            )
        }

        val scopes = 
        if (!client.grantedScopes.containsAll(scopes)) {
            throw BusinessRuleException(
                BusinessRulesError.INVALID_PARAMETERS,
                "Scope '${authorizeRequest.scope}' is not allowed for client"
            )
        }

        return client
    }
}