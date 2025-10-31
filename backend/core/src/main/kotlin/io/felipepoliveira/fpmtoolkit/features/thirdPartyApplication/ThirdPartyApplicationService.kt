package io.felipepoliveira.fpmtoolkit.features.thirdPartyApplication

import io.felipepoliveira.fpmtoolkit.BusinessRuleException
import io.felipepoliveira.fpmtoolkit.BusinessRulesError
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ThirdPartyApplicationService @Autowired constructor(
    private val thirdPartyApplicationDAO: ThirdPartyApplicationDAO,
) {
    fun findByAppId(appId: String): ThirdPartyApplicationModel {
        return thirdPartyApplicationDAO.findById(appId) as ThirdPartyApplicationModel? ?: throw BusinessRuleException(
            error = BusinessRulesError.NOT_FOUND,
            "Could not find third party application identified by appId: $appId"
        )
    }
}