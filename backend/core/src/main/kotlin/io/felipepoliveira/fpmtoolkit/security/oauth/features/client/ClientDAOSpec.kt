package io.felipepoliveira.fpmtoolkit.security.oauth.features.client

interface ClientDAOSpec {

    /**
     * Find a client identified by its ID
     */
    fun findById(clientId: String): ClientModelSpec?
}