package io.felipepoliveira.fpmtoolkit.features.oauth.client

import io.felipepoliveira.fpmtoolkit.dao.DAO
import io.felipepoliveira.fpmtoolkit.security.oauth.features.client.ClientDAOSpec

interface ThirdPartyApplicationDAO : ClientDAOSpec, DAO<Long, ThirdPartyApplicationModel> {

}