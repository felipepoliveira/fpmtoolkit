package io.felipepoliveira.fpmtoolkit.security.oauth.features.authorizationCode

interface AuthorizationCodeDAO<T: AuthorizationCodeModel> {

    fun findByCode(code: String): T?

    fun revoke(authorizationCode: T): T?

}