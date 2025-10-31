package io.felipepoliveira.fpmtoolkit.jobs

import io.felipepoliveira.fpmtoolkit.beans.AppContext
import io.felipepoliveira.fpmtoolkit.beans.AppContextProvider
import io.felipepoliveira.fpmtoolkit.features.thirdPartyApplication.ThirdPartyApplicationDAO
import io.felipepoliveira.fpmtoolkit.features.thirdPartyApplication.ThirdPartyApplicationModel
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class DevelopmentContextJobs @Autowired constructor(
    private val appContextProvider: AppContextProvider,
    private val thirdPartyApplicationDAO: ThirdPartyApplicationDAO,
) {
    @EventListener(ApplicationReadyEvent::class)
    @Transactional
    fun onDevelopmentContext() {

        // Ignore if application is not running on DEVELOPMENT context
        if (appContextProvider.getAppContext() != AppContext.DEVELOPMENT) {
            println("DevelopmentContextJobs: Not triggered because app is not on DEVELOPMENT context")
            return
        }

        // Jobs execution below
        createDefaultThirdPartyApplication()
    }

    private fun createDefaultThirdPartyApplication() {

        // ignore if client is already created
        val clientId = "6a46845f-cb3a-4d78-8da3-e6bf3772781b"
        if (thirdPartyApplicationDAO.findById(clientId) != null) {
            return
        }

        // persist the clinet
        thirdPartyApplicationDAO.persist(ThirdPartyApplicationModel(
            id = null,
            clientId = clientId,
            clientSecret = "6kjqC4k-vbqQXsOkv7BI0Bm9IHcCCd5o0nZavQi9e9o",
            name = "Default development client",
            grantedScopes = listOf(
                "read"
            ),
            allowedRedirectUris = listOf(
                "http://localhost",
//                "https://localhost",
            )
        ))
    }
}