package io.felipepoliveira.fpmtoolkit.security.oauth.features.client

interface ClientDAO<T : ClientModel> {

    /**
     * Find a client identified by its ID
     */
    fun findById(clientId: String): T?
}