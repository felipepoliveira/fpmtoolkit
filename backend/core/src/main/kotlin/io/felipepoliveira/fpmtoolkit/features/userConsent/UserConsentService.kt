package io.felipepoliveira.fpmtoolkit.features.userConsent

import io.felipepoliveira.fpmtoolkit.BaseService
import io.felipepoliveira.fpmtoolkit.BusinessRuleException
import io.felipepoliveira.fpmtoolkit.BusinessRulesError
import io.felipepoliveira.fpmtoolkit.ext.addError
import io.felipepoliveira.fpmtoolkit.features.thirdPartyApplication.ThirdPartyApplicationDAO
import io.felipepoliveira.fpmtoolkit.features.thirdPartyApplication.ThirdPartyApplicationModel
import io.felipepoliveira.fpmtoolkit.features.users.UserService
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cglib.core.Local
import org.springframework.stereotype.Service
import org.springframework.validation.SmartValidator
import java.time.LocalDateTime

@Service
class UserConsentService @Autowired constructor(
    private val userConsentDAO: UserConsentDAO,
    private val userService: UserService,
    private val thirdPartyApplicationDAO: ThirdPartyApplicationDAO,
    smartValidator: SmartValidator
) : BaseService(smartValidator) {

    @Transactional
    fun registerConsent(requesterUuid: String, thirdPartyAppId : String): UserConsentModel {

        // fetch requester account
        val requester = userService.assertFindByUuid(requesterUuid)

        // fetch and validate third party from database
        val thirdPartyApp = thirdPartyApplicationDAO.findById(thirdPartyAppId) as ThirdPartyApplicationModel? ?: throw BusinessRuleException(
            BusinessRulesError.NOT_FOUND,
            "Could not find third party app identified by $thirdPartyAppId"
        )

        // check if user has already consented with the app
        val consent = userConsentDAO.findConsent(requester, thirdPartyApp) as UserConsentModel?
        if (consent != null && consent.grants.containsAll(thirdPartyApp.grantedScopes)) {
            return consent
        }

        // if the consent already exists updated it and return it
        if (consent != null) {
            consent.grants = thirdPartyApp.grantedScopes.toSet()
            consent.consentedAt = LocalDateTime.now()
            userConsentDAO.update(consent)
            return consent
        }

        // if the consent does not exist create a new one
        val newConsent = UserConsentModel(
            id = null,
            user = requester,
            client = thirdPartyApp,
            consentedAt = LocalDateTime.now(),
            grants = thirdPartyApp.grantedScopes.toSet()
        )
        userConsentDAO.persist(newConsent)
        return newConsent
    }

    @Transactional
    fun removeConsent(requesterUuid: String, thirdPartyAppId : String): UserConsentModel {
        // fetch requester account
        val requester = userService.assertFindByUuid(requesterUuid)

        // fetch and validate third party from database
        val thirdPartyApp = thirdPartyApplicationDAO.findById(thirdPartyAppId) as ThirdPartyApplicationModel? ?: throw BusinessRuleException(
            BusinessRulesError.NOT_FOUND,
            "Could not find third party app identified by $thirdPartyAppId"
        )

        // check if user has already consented with the app
        val consent = userConsentDAO.findConsent(requester, thirdPartyApp) as UserConsentModel? ?: throw BusinessRuleException(
            BusinessRulesError.NOT_FOUND,
            "Could not find the user consent for app $thirdPartyAppId"
        )

        userConsentDAO.delete(consent)

        return consent
    }
}