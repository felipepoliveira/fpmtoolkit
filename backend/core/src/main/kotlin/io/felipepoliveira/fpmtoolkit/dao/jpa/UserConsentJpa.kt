package io.felipepoliveira.fpmtoolkit.dao.jpa

import io.felipepoliveira.fpmtoolkit.ext.fetchFirst
import io.felipepoliveira.fpmtoolkit.features.userConsent.UserConsentDAO
import io.felipepoliveira.fpmtoolkit.features.userConsent.UserConsentModel
import io.felipepoliveira.fpmtoolkit.security.oauth.features.client.ClientModelSpec
import io.felipepoliveira.fpmtoolkit.security.oauth.features.user.UserModelSpec
import io.felipepoliveira.fpmtoolkit.security.oauth.features.userConsent.UserConsentModelSpec
import org.springframework.stereotype.Repository

@Repository
class UserConsentJpa : UserConsentDAO, BaseJpa<Long, UserConsentModel>() {

    override fun findConsent(user: UserModelSpec, client: ClientModelSpec): UserConsentModelSpec? {
        return query("c")
            .where("c.user.uuid = :userId")
            .and("c.client.clientId = :clientId")
            .setParameter("userId", user.userId)
            .setParameter("clientId", client.clientId)
            .prepare()
            .fetchFirst()
    }

    override fun getModelType() = UserConsentModel::class.java
}