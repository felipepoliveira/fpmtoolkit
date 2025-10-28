package io.felipepoliveira.fpmtoolkit.features.oauth.authorizationCode

import com.fasterxml.jackson.databind.ObjectMapper
import io.felipepoliveira.fpmtoolkit.cache.CacheHandler
import io.felipepoliveira.fpmtoolkit.security.oauth.features.authorizationCode.AuthorizationCodeDAOSpec
import io.felipepoliveira.fpmtoolkit.security.oauth.features.authorizationCode.AuthorizationCodeModelSpec
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository
import java.time.Duration
import java.time.LocalDateTime

@Repository
class AuthorizationCodeCache @Autowired constructor(
    private val cacheHandler: CacheHandler,
    private val objectMapper: ObjectMapper,
) : AuthorizationCodeDAOSpec {

    private fun generateKey(code: String): String {
        return "oauth-authorizationCode-$code"
    }

    override fun findByCode(code: String): AuthorizationCodeModelSpec? {
        val cachedAccessToken = cacheHandler.get(generateKey(code)) ?: return null
        return objectMapper.readValue(cachedAccessToken, AuthorizationCodeModel::class.java)
    }

    override fun persist(authorizationCode: AuthorizationCodeModelSpec): AuthorizationCodeModelSpec {
        cacheHandler.put(
            generateKey(authorizationCode.code),
            objectMapper.writeValueAsString(authorizationCode),
            Duration.between(LocalDateTime.now(), authorizationCode.expiresAt)
        )
        return authorizationCode
    }

    override fun revoke(authorizationCode: AuthorizationCodeModelSpec): AuthorizationCodeModelSpec {
        cacheHandler.delete(generateKey(authorizationCode.code))
        return authorizationCode
    }
}