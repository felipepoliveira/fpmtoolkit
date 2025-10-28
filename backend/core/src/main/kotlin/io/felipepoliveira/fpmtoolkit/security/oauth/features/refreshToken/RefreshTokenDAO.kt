package io.felipepoliveira.fpmtoolkit.security.oauth.features.refreshToken

import io.felipepoliveira.fpmtoolkit.security.oauth.features.accessToken.AccessTokenModel

interface RefreshTokenDAO {
    /**
     * Find an access token identified by its ID
     */
    fun findById(tokenId: String): AccessTokenModel

    /**
     * Revoke a AccessTokenModel
     */
    fun revoke(token: AccessTokenModel): AccessTokenModel
}