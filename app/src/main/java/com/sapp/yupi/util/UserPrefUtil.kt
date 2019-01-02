package com.sapp.yupi.util

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.sapp.yupi.ui.appintro.PREF_EMAIL
import com.sapp.yupi.ui.appintro.PREF_EMAIL_PASS
import com.sapp.yupi.ui.appintro.PREF_PHONE
import com.sapp.yupi.ui.appintro.USER_PREFERENCES

class UserPrefUtil {
    companion object {
        private lateinit var pref: SharedPreferences

        fun init(context: Context) {
            // Initialize preferences
            pref = context.getSharedPreferences(USER_PREFERENCES, Context.MODE_PRIVATE)
        }

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