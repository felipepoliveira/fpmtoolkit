package io.felipepoliveira.fpmtoolkit.security.oauth.features.refreshToken

import io.felipepoliveira.fpmtoolkit.features.oauth.refreshToken.RefreshTokenModel

interface RefreshTokenDAOSpec {
    /**
     * Find an access token identified by its ID
     */
    fun findByToken(token: String): RefreshTokenModelSpec?

    fun persist(token: RefreshTokenModelSpec): RefreshTokenModelSpec

    /**
     * Revoke a AccessTokenModel
     */
    fun revoke(token: RefreshTokenModelSpec): RefreshTokenModelSpec
}