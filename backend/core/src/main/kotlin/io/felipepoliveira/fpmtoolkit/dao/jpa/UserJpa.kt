package io.felipepoliveira.fpmtoolkit.dao.jpa

import io.felipepoliveira.fpmtoolkit.ext.fetchFirst
import io.felipepoliveira.fpmtoolkit.features.users.UserDAO
import io.felipepoliveira.fpmtoolkit.features.users.UserModel
import io.felipepoliveira.fpmtoolkit.security.oauth.features.user.UserModelSpec
import org.springframework.stereotype.Repository

@Repository
class UserJpa : BaseJpa<Long, UserModel>(), UserDAO {

    override fun findByPrimaryEmail(primaryEmail: String): UserModel? {
        return query("user")
            .where("user.primaryEmail = :primaryEmail")
            .prepare()
            .setParameter("primaryEmail", primaryEmail)
            .fetchFirst()
    }

    override fun findByUuid(uuid: String): UserModel? {
        return query("user").where("user.uuid = :uuid")
            .prepare().setParameter("uuid", uuid)
            .fetchFirst()
    }

    override fun findById(userId: String): UserModelSpec? {
        return findByUuid(userId)
    }

    override fun getModelType() = UserModel::class.java
}