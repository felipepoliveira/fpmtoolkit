package io.felipepoliveira.fpmtoolkit.io.felipepoliveira.fpmtoolkit.security.oauth.features.dynamicClient

interface DynamicClientDAOSpec {

    /**
     * This method is automatically called by the specification when a new dynamic client should be registered in the
     * platform. If the
     */
    fun persist(data: DynamicClientModelSpec): DynamicClientModelSpec

}