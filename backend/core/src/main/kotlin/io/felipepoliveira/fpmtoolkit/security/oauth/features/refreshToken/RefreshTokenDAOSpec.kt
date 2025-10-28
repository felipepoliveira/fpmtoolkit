package io.felipepoliveira.fpmtoolkit.security.oauth.features.refreshToken

import io.felipepoliveira.fpmtoolkit.features.oauth.refreshToken.RefreshTokenModel

interface RefreshTokenDAOSpec {
    /**
     * Find an access token identified by its ID
     */
    fun findById(tokenId: String): RefreshTokenModel?

    fun persist(token: RefreshTokenModel): RefreshTokenModel

    /**
     * Revoke a AccessTokenModel
     */
    fun revoke(token: RefreshTokenModel): RefreshTokenModel
}