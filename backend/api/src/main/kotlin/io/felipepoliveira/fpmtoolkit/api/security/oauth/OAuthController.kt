package io.felipepoliveira.fpmtoolkit.api.security.oauth

import io.felipepoliveira.fpmtoolkit.BusinessRuleException
import io.felipepoliveira.fpmtoolkit.BusinessRulesError
import io.felipepoliveira.fpmtoolkit.api.controllers.BaseRestController
import io.felipepoliveira.fpmtoolkit.api.security.auth.RequestClient
import io.felipepoliveira.fpmtoolkit.api.security.oauth.dto.AuthorizeRequest
import io.felipepoliveira.fpmtoolkit.api.security.oauth.OAuthService
import io.felipepoliveira.fpmtoolkit.api.security.oauth.features.client.ClientModel
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
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
        val consent = if (requestClient != null) authService.tryFindConsentAndValidate(requestClient, client, params) else null

        // If the client is already consent send directly to the redirectUri informed by it
        if (consent != null) {

        }

        // otherwise send to the consent front-end page
        val redirectUri = UriComponentsBuilder
            .fromUriString("https://localhost:3000/oauth2/code/oidc-client")
            .toUriString("code")

        return redirect(redirectUri)
    }

    private fun validateAuthorizeRequest(authorizeRequest: AuthorizeRequest): ClientModel {
        val client = authService.findClientById(authorizeRequest.clientId)

        // check if given redirect_uri can be used
        if (!client.allowedRedirectUris.contains(authorizeRequest.redirectUri)) {
            throw BusinessRuleException(
                BusinessRulesError.INVALID_PARAMETERS,
                "Redirect uri ${authorizeRequest.redirectUri} is not allowed for client"
            )
        }

        val scopes = authorizeRequest.scope.split(" ")
        if (!client.grantedScopes.containsAll(scopes)) {
            throw BusinessRuleException(
                BusinessRulesError.INVALID_PARAMETERS,
                "Scope '${authorizeRequest.scope}' is not allowed for client"
            )
        }

        return client
    }
}