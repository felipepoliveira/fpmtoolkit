package io.felipepoliveira.fpmtoolkit.security.oauth.features.user

interface UserDAO<T : UserModel> {

    /**
     * Find a user identified by its ID
     */
    fun findById(userId: String): T?
}