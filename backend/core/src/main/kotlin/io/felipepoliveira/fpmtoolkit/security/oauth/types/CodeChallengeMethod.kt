package io.felipepoliveira.fpmtoolkit.security.oauth.types

import io.felipepoliveira.fpmtoolkit.BusinessRuleException
import io.felipepoliveira.fpmtoolkit.BusinessRulesError
import java.security.MessageDigest
import java.util.Base64

/**
 * Represents the different types for CodeChallengeMethod
 *
 * The code_verifier is a unique high-entropy cryptographically random string generated for each authorization request,
 * using the unreserved characters [A-Z] / [a-z] / [0-9] / "-" / "." / "_" / "~",
 * with a minimum length of 43 characters and a maximum length of 128 characters.
 *
 * The client stores the code_verifier temporarily, and calculates the code_challenge which it uses in the authorization request.
 */
enum class CodeChallengeMethod {
    /**
     *  code_challenge = BASE64URL-ENCODE(SHA256(ASCII(code_verifier)))
     */
    S256,

    /**
     *  code_challenge = code_verifier
     */
    PLAIN

    ;


    fun hash(codeVerifier: String): String {
        return when (this) {
            PLAIN -> codeVerifier
            S256 -> hashSha256(codeVerifier)
        }
    }

    private fun hashSha256(codeVerifier: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashed = digest.digest(codeVerifier.toByteArray(Charsets.US_ASCII))

        val encoded = Base64.getUrlEncoder()
            .withoutPadding()
            .encodeToString(hashed)

        return encoded
    }



        companion object {
        fun fromAuthorizeRequest(authorizeRequest: AuthorizeRequestSpec): CodeChallengeMethod? {
            return when (authorizeRequest.codeChallengeMethod) {
                "S256" -> CodeChallengeMethod.S256
                "plain" -> CodeChallengeMethod.PLAIN
                null -> null
                else -> throw BusinessRuleException(
                    error = BusinessRulesError.INVALID_PARAMETERS,
                    "Invalid 'code_challenge_method': ${authorizeRequest.codeChallengeMethod}. Use: [S256, plain]"
                )
            }
        }
    }
}