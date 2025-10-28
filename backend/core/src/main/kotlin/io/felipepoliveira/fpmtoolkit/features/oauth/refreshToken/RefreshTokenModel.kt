package io.felipepoliveira.fpmtoolkit.features.oauth.refreshToken

import io.felipepoliveira.fpmtoolkit.security.oauth.features.refreshToken.RefreshTokenModelSpec
import java.time.LocalDateTime

class RefreshTokenModel(
    override val id: String,
    override val token: String,
    override val expiresAt: LocalDateTime
) : RefreshTokenModelSpec