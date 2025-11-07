package io.felipepoliveira.fpmtoolkit.features.oauth.refreshToken

import com.fasterxml.jackson.databind.ObjectMapper
import io.felipepoliveira.fpmtoolkit.cache.CacheHandler
import io.felipepoliveira.fpmtoolkit.security.oauth.features.refreshToken.RefreshTokenDAOSpec
import io.felipepoliveira.fpmtoolkit.security.oauth.features.refreshToken.RefreshTokenModelSpec
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository
import java.time.Duration
import java.time.LocalDateTime

@Repository
class RefreshTokenCache @Autowired constructor(
    private val cacheHandler: CacheHandler,
    private val objectMapper: ObjectMapper,
) : RefreshTokenDAOSpec {

    private fun generateKey(code: String): String {
        return "oauth-refreshToken-$code"
    }

    override fun findByToken(token: String): RefreshTokenModelSpec? {
        val cachedRefreshToken = cacheHandler.get(generateKey(token)) ?: return null
        return objectMapper.readValue(cachedRefreshToken, RefreshTokenModel::class.java)
    }

    override fun persist(token: RefreshTokenModelSpec): RefreshTokenModelSpec {
        cacheHandler.put(
            generateKey(token.token),
            objectMapper.writeValueAsString(token),
            Duration.between(LocalDateTime.now(), token.expiresAt)
        )
        return token
    }

    override fun revoke(token: RefreshTokenModelSpec): RefreshTokenModelSpec {
        cacheHandler.delete(generateKey(token.token))
        return token
    }
}