package io.felipepoliveira.fpmtoolkit.api.security.oauth.features.client

interface ClientDAO<T : ClientModel> {

    /**
     * Find a client identified by its ID
     */
    fun findById(clientId: String): T?
}