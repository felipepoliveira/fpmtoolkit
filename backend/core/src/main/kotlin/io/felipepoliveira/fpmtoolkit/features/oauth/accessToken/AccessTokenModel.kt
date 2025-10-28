package io.felipepoliveira.fpmtoolkit.features.oauth.accessToken

import io.felipepoliveira.fpmtoolkit.security.oauth.features.accessToken.AccessTokenModelSpec
import java.time.LocalDateTime

class AccessTokenModel(
    override val id: String,
    override val token: String,
    override val issuedAt: LocalDateTime,
    override val expiresAt: LocalDateTime
) : AccessTokenModelSpec {
}