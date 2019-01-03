package com.sapp.yupi.util

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

const val USER_PREFERENCES = "user_preferences"

const val PREF_PHONE = "phone"
const val PREF_EMAIL = "email"
const val PREF_EMAIL_PASS = "email_pass"

const val PREF_FIRST_LAUNCH = "first_launch"

class UserPrefUtil {
    companion object {
        private lateinit var pref: SharedPreferences

        fun init(context: Context) {
            // Initialize preferences
            pref = context.getSharedPreferences(USER_PREFERENCES, Context.MODE_PRIVATE)
        }

        fun check(): Boolean = !isFirstLaunch() && getEmail().isNotEmpty() &&
                getEmailPass().isNotEmpty() && getPhone().isNotEmpty()

        fun setFirstLaunch() {
            pref.edit { putBoolean(PREF_FIRST_LAUNCH, false) }
        }

        private fun isFirstLaunch() = pref.getBoolean(PREF_FIRST_LAUNCH, true)

        fun setEmail(email: String) {
            pref.edit { putString(PREF_EMAIL, email) }
        }

        fun getEmail() = pref.getString(PREF_EMAIL, "")!!

        fun setEmailPass(pass: String) {
            pref.edit { putString(PREF_EMAIL_PASS, pass) }
        }

        fun getEmailPass() = pref.getString(PREF_EMAIL_PASS, "")!!

        fun setPhone(phone: String) {
            pref.edit { putString(PREF_PHONE, phone) }
        }

        fun getPhone() = pref.getString(PREF_PHONE, "")!!
    }
}