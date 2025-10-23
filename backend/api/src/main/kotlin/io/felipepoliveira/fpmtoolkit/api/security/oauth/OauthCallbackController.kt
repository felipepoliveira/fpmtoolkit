package io.felipepoliveira.fpmtoolkit.api.security.oauth

import io.felipepoliveira.fpmtoolkit.api.controllers.BaseRestController
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class OauthCallbackController : BaseRestController() {

    @GetMapping("/login/oauth2/code/oidc-client")
    fun handleCallback() = ok {
        "Authorization code recebido"
    }

}