package io.felipepoliveira.fpmtoolkit.security.oauth.features.accessToken

interface AccessTokenDAOSpec {
    /**
     * Find an access token identified by its ID
     */
    fun findById(tokenId: String): AccessTokenModelSpec?

    /**
     * Persist a access token
     */
    fun persist(token: AccessTokenModelSpec): AccessTokenModelSpec

    /**
     * Revoke a AccessTokenModel
     */
    fun revoke(token: AccessTokenModelSpec): AccessTokenModelSpec
}