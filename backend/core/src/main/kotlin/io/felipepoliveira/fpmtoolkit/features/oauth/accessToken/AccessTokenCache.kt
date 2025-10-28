package io.felipepoliveira.fpmtoolkit.features.oauth.accessToken

import com.fasterxml.jackson.databind.ObjectMapper
import io.felipepoliveira.fpmtoolkit.cache.CacheHandler
import io.felipepoliveira.fpmtoolkit.security.oauth.features.accessToken.AccessTokenDAOSpec
import io.felipepoliveira.fpmtoolkit.security.oauth.features.accessToken.AccessTokenModelSpec
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository
import java.time.Duration
import java.time.LocalDateTime

@Repository
class AccessTokenCache @Autowired constructor(
    private val cacheHandler: CacheHandler,
    private val objectMapper: ObjectMapper,
) : AccessTokenDAOSpec {

    private fun generateKey(tokenId: String): String {
        return "oauth-accessToken-$tokenId"
    }

    override fun findById(tokenId: String): AccessTokenModelSpec? {
        val key = generateKey(tokenId)
        val cachedAccessToken = cacheHandler.get(key) ?: return null
        return objectMapper.readValue(cachedAccessToken, AccessTokenModel::class.java)
    }

    override fun persist(token: AccessTokenModelSpec): AccessTokenModelSpec {
        cacheHandler.put(
            generateKey(token.id), objectMapper.writeValueAsString(token), Duration.between(LocalDateTime.now(), token.expiresAt)
        )
        return token
    }

    override fun revoke(token: AccessTokenModelSpec): AccessTokenModelSpec {
        cacheHandler.delete(generateKey(token.id))
        return token
    }

}
