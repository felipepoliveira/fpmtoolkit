package io.felipepoliveira.fpmtoolkit.api.security.oauth

import io.felipepoliveira.fpmtoolkit.api.controllers.BaseRestController
import io.felipepoliveira.fpmtoolkit.api.security.oauth.dto.ProtectedResourceResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/.well-known")
class WellKnownOAuthController : BaseRestController() {

    companion object {
        // Protected Resource Metadata
        val protectedResourceResponse = ProtectedResourceResponse(
            resource = "http://localhost:8080",
            authorizationServers = arrayOf(
                "http://localhost:8080/oauth"
            )
        )

        // Authorization Server Metadata
        val authorizationServerMetadata = mapOf(
            "issuer" to "http://localhost:8080/oauth",
            "authorization_endpoint" to "http://localhost:8080/oauth/authorize",
            "registration_endpoint" to "http://localhost:8080/oauth/register",
            "token_endpoint" to "http://localhost:8080/oauth/token",
//            "introspection_endpoint" to "http://localhost:8080/oauth2/introspect",
//            "jwks_uri" to "http://localhost:8080/oauth2/jwks",
            "response_types_supported" to listOf("code", "token"),
            "grant_types_supported" to listOf("authorization_code", "client_credentials"),
            "token_endpoint_auth_methods_supported" to listOf("client_secret_basic", "private_key_jwt")
        )
    }

    @GetMapping("/oauth-protected-resource")
    fun oAuthProtectedResource() = ok {
        protectedResourceResponse
    }

    @GetMapping("/oauth-authorization-server")
    fun oAuthAuthorizationServer() = ok {
        authorizationServerMetadata
    }
}