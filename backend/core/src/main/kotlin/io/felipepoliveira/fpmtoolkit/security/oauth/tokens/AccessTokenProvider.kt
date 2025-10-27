package io.felipepoliveira.fpmtoolkit.io.felipepoliveira.fpmtoolkit.security.oauth.tokens

import com.auth0.jwt.JWT
import io.felipepoliveira.fpmtoolkit.security.oauth.features.userConsent.UserConsentModel
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class AccessTokenProvider {

    fun issue(userConsent: UserConsentModel, expiresAt: LocalDateTime): String {
        JWT
            .create()
            .iss
    }
}