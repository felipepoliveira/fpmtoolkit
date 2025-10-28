package io.felipepoliveira.fpmtoolkit.security.oauth.features.authorizationCode

interface AuthorizationCodeDAOSpec {

    fun findByCode(code: String): AuthorizationCodeModelSpec?

    fun persist(authorizationCode: AuthorizationCodeModelSpec): AuthorizationCodeModelSpec

    fun revoke(authorizationCode: AuthorizationCodeModelSpec): AuthorizationCodeModelSpec

}