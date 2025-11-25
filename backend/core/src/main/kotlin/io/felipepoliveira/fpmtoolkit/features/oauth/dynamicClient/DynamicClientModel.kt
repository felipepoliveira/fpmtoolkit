package io.felipepoliveira.fpmtoolkit.io.felipepoliveira.fpmtoolkit.features.oauth.dynamicClient

import io.felipepoliveira.fpmtoolkit.features.thirdPartyApplication.ThirdPartyApplicationModel
import io.felipepoliveira.fpmtoolkit.io.felipepoliveira.fpmtoolkit.security.oauth.features.dynamicClient.DynamicClientModelSpec
import io.felipepoliveira.fpmtoolkit.security.oauth.features.client.ClientModelSpec
import java.time.Instant

class DynamicClientModel(
    override val clientId: String,
    override val clientName: String,
    override val allowedRedirectUris: List<String>,
    override val grantedScopes: List<String>,
    override val clientSecret: String,
    override val clientIdIssuedAt: Instant?,
    override val clientSecretExpiresAt: Instant?
) : DynamicClientModelSpec {


    override fun toClient(): ClientModelSpec {
        return ThirdPartyApplicationModel(
            id = null,
            clientId = clientId,
            name = clientName,
            clientSecret = clientSecret,
            allowedRedirectUris = allowedRedirectUris,
            grantedScopes = grantedScopes
        )
    }
}