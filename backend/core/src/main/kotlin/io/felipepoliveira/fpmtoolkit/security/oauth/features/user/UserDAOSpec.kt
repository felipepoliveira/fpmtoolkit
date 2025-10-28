package io.felipepoliveira.fpmtoolkit.security.oauth.features.user

interface UserDAOSpec {

    /**
     * Find a user identified by its ID
     */
    fun findById(userId: String): UserModelSpec?
}