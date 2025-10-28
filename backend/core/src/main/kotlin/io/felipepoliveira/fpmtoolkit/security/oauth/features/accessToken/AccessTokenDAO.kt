package io.felipepoliveira.fpmtoolkit.security.oauth.features.accessToken

interface AccessTokenDAO {
    /**
     * Find an access token identified by its ID
     */
    fun findById(tokenId: String): AccessTokenModel

    /**
     * Revoke a AccessTokenModel
     */
    fun revoke(token: AccessTokenModel): AccessTokenModel
}