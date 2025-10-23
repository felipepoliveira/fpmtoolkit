package io.felipepoliveira.fpmtoolkit.api.security.oauth

import org.springframework.security.oauth2.core.AuthorizationGrantType
import org.springframework.security.oauth2.core.ClientAuthenticationMethod
import org.springframework.security.oauth2.core.oidc.OidcScopes
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class ApiRegisteredClientRepository : RegisteredClientRepository {

    companion object {
        private val registeredClient = RegisteredClient.withId(UUID.randomUUID().toString())
            .clientId("oidc-client")
            .clientSecret("{noop}secret")
            .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
            .redirectUri("http://127.0.0.1:8080/login/oauth2/code/oidc-client")
            .postLogoutRedirectUri("http://127.0.0.1:8080/")
            .scope(OidcScopes.OPENID)
            .scope(OidcScopes.PROFILE)
            .clientSettings(ClientSettings.builder().requireAuthorizationConsent(true).build())
            .build();
    }

    override fun save(registeredClient: RegisteredClient?) {}

    override fun findById(id: String?): RegisteredClient? {
        return if (id == registeredClient.id) {
            registeredClient
        } else {
            null
        }
    }

    override fun findByClientId(clientId: String?): RegisteredClient? {
        return if (clientId == registeredClient.clientId) {
            registeredClient
        } else {
            null
        }
    }
}