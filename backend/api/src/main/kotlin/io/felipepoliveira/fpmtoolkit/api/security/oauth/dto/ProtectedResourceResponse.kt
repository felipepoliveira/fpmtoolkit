package io.felipepoliveira.fpmtoolkit.api.security.oauth.dto

import com.fasterxml.jackson.annotation.JsonProperty

class ProtectedResourceResponse(

    @field:JsonProperty(value = "resource")
    val resource: String,

    @field:JsonProperty(value = "authorization_servers")
    val authorizationServers: Array<String>?,
)