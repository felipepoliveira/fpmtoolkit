package io.felipepoliveira.fpmtoolkit.api.controllers

import io.felipepoliveira.fpmtoolkit.api.security.auth.RequestClient
import io.felipepoliveira.fpmtoolkit.features.userConsent.UserConsentService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/third-party-app")
class ThirdPartyApplicationController @Autowired constructor(
    private val userConsentService: UserConsentService,
) : BaseRestController(){

    @PostMapping("/{appId}/consent")
    fun addConsent(
        @AuthenticationPrincipal requestClient: RequestClient,
        @PathVariable appId: String
    ) = ok {
        userConsentService.registerConsent(requestClient.userIdentifier, appId)
    }

    @DeleteMapping("/{appId}/consent")
    fun removeConsent(
        @AuthenticationPrincipal requestClient: RequestClient,
        @PathVariable appId: String
    ) = ok {
        userConsentService.removeConsent(requestClient.userIdentifier, appId)
    }

}