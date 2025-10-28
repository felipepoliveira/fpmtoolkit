package io.felipepoliveira.fpmtoolkit.i18n

import io.felipepoliveira.fpmtoolkit.commons.i18n.I18nRegion
import io.felipepoliveira.fpmtoolkit.commons.io.WildcardString
import io.felipepoliveira.fpmtoolkit.commons.io.getLocalizedResourceAsInputStream
import java.util.*

class I18n {
    companion object {
        /**
         * Store all messages contained
         */
        private var messages: MutableMap<I18nRegion, Properties> = mutableMapOf()

        /*
         * Load all i18n.properties files in /resources/i18n/{{region}}.properties
         */
        init {
            for (region in I18nRegion.entries) {
                val messageFile = Properties()
                messageFile.load(getLocalizedResourceAsInputStream("/i18n/{{region}}.properties", region))
                messages[region] = messageFile
            }

            validateMessagesIntegrity()
        }

        private fun validateMessagesIntegrity() {

            // check if messages exists
            if (messages.isEmpty()) {
                throw IllegalStateException("I18n messages map is empty. Add /resources/i18n/{{region}}.properties files to fix this error")
            }

            // validate if all messages that contains wildcards is present across all
            // different regions
            val wildcardsPerPropertyMap = mutableMapOf<String, Set<String>>()

            val messagerIter = messages.iterator()
            val firstMessagesEntry = messagerIter.next()

            // process all props from the first messages entry as wildcards string so it can extract all wildcards
            // the first entry of the messages will serve as a reference to the other files
            val referenceWildcardsMap = firstMessagesEntry.value
                .map { (key, value) ->
                    key.toString() to WildcardString(value.toString()).wildcards
                }
                .toMap(mutableMapOf())

            // iterate over the other props so it can infer if it has all the props and wildcards from the reference
            for (otherMessagesEntry in messagerIter) {

                val currentWildcardsMap = otherMessagesEntry.value
                    .map { (key, value) ->
                        key.toString() to WildcardString(value.toString()).wildcards
                    }
                    .toMap(mutableMapOf())


                // Check for missing keys in both maps
                val referenceKeys = referenceWildcardsMap.keys
                val currentKeys = currentWildcardsMap.keys

                // union of differences → all keys that don't match between maps
                val mismatchedKeys = (referenceKeys - currentKeys) + (currentKeys - referenceKeys)

                if (mismatchedKeys.isNotEmpty()) {
                    throw IllegalStateException(
                        "Message file '${otherMessagesEntry.key}' has mismatched keys: $mismatchedKeys"
                    )
                }

                //
                val mismatchedWildcards = currentWildcardsMap
                    .map { (key, wildcards) ->
                    val refWildcards = referenceWildcardsMap[key] ?: throw Exception(
                        "Unexpected error: Could not find $key on referenceWildcardsMap"
                    )
                    key to (wildcards - refWildcards) + (refWildcards - wildcards)
                }.toMap().filter { (_, value) -> value.isNotEmpty() }


                if (mismatchedWildcards.isNotEmpty()) {
                    throw IllegalStateException(
                        "Some mismatched wildcards were found in i18n file ${otherMessagesEntry.key}: " +
                                "${mismatchedWildcards.filter { (k, v) ->  v.isNotEmpty()}}"
                    )
                }
            }
        }

        /**
         * Return a localized message derived from /resources/i18n/{{region}}.properties files. If the given
         * region or messageKey is not present an exception will be thrown
         */
        fun getMessage(region: I18nRegion, messageKey: String): String {
            val localizedMessages = messages[region] ?: throw IllegalArgumentException(
                "Could not find region $region in I18n loaded files"
            )
            return localizedMessages.getProperty(messageKey) ?: throw IllegalArgumentException(
                "Could not find message $messageKey in i18n messages"
            )
        }
    }

    /**
     * Store all supported message keys
     */
    class MessageKeys {
        companion object {
            const val MAIL_ORGANIZATION_INVITE_TITLE = "mail.organizationInvite.title"
            const val MAIL_PASSWORD_RECOVERY_TITLE = "mail.passwordRecovery.title"
            const val MAIL_PRIMARY_EMAIL_CHANGE_TITLE = "mail.primaryEmailChange.title"
            const val MAIL_PRIMARY_EMAIL_CONFIRMATION_TITLE = "mail.primaryEmailConfirmation.title"
            const val MAIL_WELCOME = "mail.welcome.title"
        }
    }
}