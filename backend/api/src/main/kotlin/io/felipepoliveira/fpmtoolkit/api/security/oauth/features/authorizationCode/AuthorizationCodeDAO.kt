package io.felipepoliveira.fpmtoolkit.api.security.oauth.features.authorizationCode

interface AuthorizationCodeDAO<T: AuthorizationCodeModel> {

    fun findByCode(code: String): T?

    fun persist(authorizationCode: T): T

    fun revoke(authorizationCode: T): T?

}