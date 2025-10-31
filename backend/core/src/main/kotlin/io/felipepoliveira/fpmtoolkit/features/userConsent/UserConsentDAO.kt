package io.felipepoliveira.fpmtoolkit.features.userConsent

import io.felipepoliveira.fpmtoolkit.dao.DAO
import io.felipepoliveira.fpmtoolkit.security.oauth.features.userConsent.UserConsentDAOSpec

interface UserConsentDAO : DAO<Long, UserConsentModel>, UserConsentDAOSpec {
}