package io.felipepoliveira.fpmtoolkit.features.oauth.refreshToken

import io.felipepoliveira.fpmtoolkit.security.oauth.features.refreshToken.RefreshTokenModelSpec
import java.time.LocalDateTime

class RefreshTokenModel(
    override val token: String,
    override val expiresAt: LocalDateTime,
    override val userId: String,
    override val clientId: String,
    override val issuedAt: LocalDateTime
) : RefreshTokenModelSpec