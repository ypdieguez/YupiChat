package com.sapp.yupi

import android.content.Context
import androidx.core.content.edit
import com.google.i18n.phonenumbers.NumberParseException
import com.google.i18n.phonenumbers.PhoneNumberUtil

class UserPref private constructor(context: Context) {

    private var pref = context.getSharedPreferences(USER_PREFERENCES, Context.MODE_PRIVATE)

    fun isReady() = from.isNotEmpty() && email.isNotEmpty() && pass.isNotEmpty() && phone.isNotEmpty()
            && phoneValidated && emailValidated && passValidated

    /**
     * Where the user are from
     */
    var from
    get() = pref.getString(PREF_FROM, "")!!
    set(value) = pref.edit { putString(PREF_FROM, value) }

    /**
     * Email config
     */
    var email
        get() = pref.getString(PREF_EMAIL, "")!!
        set(value) = pref.edit { putString(PREF_EMAIL, value) }

    var pass
        get() = pref.getString(PREF_EMAIL_PASS, "")!!
        set(value) = pref.edit { putString(PREF_EMAIL_PASS, value) }

    var host
        get() = pref.getString(PREF_HOST, "")!!
        set(value) = pref.edit { putString(PREF_HOST, value) }

    var port
        get() = pref.getString(PREF_PORT, "")!!
        set(value) = pref.edit { putString(PREF_PORT, value) }

    var sslEnabled
        get() = pref.getString(PREF_SSL_ENABLED, "")!!
        set(value) = pref.edit { putString(PREF_SSL_ENABLED, value) }

    /**
     * Phone
     */
    var phone
        get() = pref.getString(PREF_PHONE, "")!!
        set(value) = pref.edit { putString(PREF_PHONE, value) }

    val regionCode: String?
        get() {
            return try {
                val phoneNumberUtil = PhoneNumberUtil.getInstance()
                val phoneNumber = phoneNumberUtil.parse(phone, null)

                phoneNumberUtil.getRegionCodeForNumber(phoneNumber)
            } catch (e: NumberParseException) {
                null
            }
        }

    /**
     * Check validation
     */
    var emailValidated
        get() = pref.getBoolean(PREF_EMAIL_VALIDATED, false)
        set(value) = pref.edit { putBoolean(PREF_EMAIL_VALIDATED, value) }

    var passValidated
        get() = pref.getBoolean(PREF_PASS_VALIDATED, false)
        set(value) = pref.edit { putBoolean(PREF_PASS_VALIDATED, value) }

    var phoneValidated
        get() = pref.getBoolean(PREF_PHONE_VALIDATED, false)
        set(value) = pref.edit { putBoolean(PREF_PHONE_VALIDATED, value) }

    companion object {

        const val USER_PREFERENCES = "user_preferences"

        const val PREF_PHONE = "phone"
        const val PREF_EMAIL = "email"
        const val PREF_EMAIL_PASS = "email_pass"

        const val PREF_EMAIL_VALIDATED = "email_validated"
        const val PREF_PASS_VALIDATED = "pass_validated"
        const val PREF_PHONE_VALIDATED = "phone_validated"

        const val PREF_HOST = "host"
        const val PREF_PORT = "port"
        const val PREF_SSL_ENABLED = "ssl_enabled"

        const val PREF_FROM = "from"

        public const val IN_CUBA = "in_cuba"
        public const val OUTSIDE_CUBA = "ouside_cuba"

        @Volatile
        private var instance: UserPref? = null

        fun getInstance(context: Context): UserPref {
            return instance ?: synchronized(this) {
                instance
                        ?: init(context).also { instance = it }
            }
        }

        private fun init(context: Context) = UserPref(context)
    }
}