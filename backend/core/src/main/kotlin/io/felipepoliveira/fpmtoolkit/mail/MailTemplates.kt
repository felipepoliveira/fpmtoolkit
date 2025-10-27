package io.felipepoliveira.fpmtoolkit.mail

import io.felipepoliveira.fpmtoolkit.commons.i18n.I18nRegion
import io.felipepoliveira.fpmtoolkit.commons.io.WildcardString
import io.felipepoliveira.fpmtoolkit.commons.io.getLocalizedResourceAsInputStream
import java.io.InputStream

class MailTemplates {
    companion object {

        const val PASSWORD_RECOVERY_MT_RP = "/mails/features/users/passwordRecovery/passwordRecovery.{{region}}.html"

        private val ALL_MAIL_TEMPLATE_RESOURCE_PATHS: Array<String> = arrayOf(
            PASSWORD_RECOVERY_MT_RP
        )

        init {
            val i18nRegionsIter = I18nRegion.entries.iterator()
            val referenceI18nRegion = i18nRegionsIter.next()

            val referenceI18nRegionWildcardsMap = ALL_MAIL_TEMPLATE_RESOURCE_PATHS.associateWith { resPath ->
                WildcardString(getLocalizedResourceAsInputStream(resPath, referenceI18nRegion)).wildcards
            }

            for (comparedI18nRegion in i18nRegionsIter) {
                val comparedI18nRegionWildcardsMap = ALL_MAIL_TEMPLATE_RESOURCE_PATHS.associateWith { resPath ->
                    WildcardString(getLocalizedResourceAsInputStream(resPath, comparedI18nRegion)).wildcards
                }

                for (comparedI18nRegionWildcardsEntry in comparedI18nRegionWildcardsMap) {
                    val referenceI18nRegionWildcards = referenceI18nRegionWildcardsMap[comparedI18nRegionWildcardsEntry.key]
                        ?: throw IllegalStateException(
                            "There is no "
                        )

                }
            }

        }

        fun getTemplateByResourcePath(resourcePath: String, region: I18nRegion) = getLocalizedResourceAsInputStream(
            resourcePath, region
        )
    }
}