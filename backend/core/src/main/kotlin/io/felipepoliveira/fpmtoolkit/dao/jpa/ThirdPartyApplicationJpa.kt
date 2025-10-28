package io.felipepoliveira.fpmtoolkit.dao.jpa

import io.felipepoliveira.fpmtoolkit.ext.fetchFirst
import io.felipepoliveira.fpmtoolkit.features.oauth.client.ThirdPartyApplicationDAO
import io.felipepoliveira.fpmtoolkit.features.oauth.client.ThirdPartyApplicationModel
import io.felipepoliveira.fpmtoolkit.security.oauth.features.client.ClientModelSpec
import org.springframework.stereotype.Repository

@Repository
class ThirdPartyApplicationJpa : ThirdPartyApplicationDAO, BaseJpa<Long, ThirdPartyApplicationModel>() {

    override fun findById(clientId: String): ClientModelSpec? {
        return query("t")
            .where("t.clientId = :clientId")
            .setParameter("clientId", clientId)
            .prepare()
            .fetchFirst()
    }

    override fun getModelType() = ThirdPartyApplicationModel::class.java
}