package com.sapp.yupi.util

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.sapp.yupi.ui.appintro.USER_PREFERENCES
import java.util.*
import kotlin.collections.HashSet

class MailSelector {
    companion object {
        private lateinit var pref: SharedPreferences
        private var mails: HashSet<String>? = null

        fun init(context: Context) {
            // Initialize preferences
            pref = context.getSharedPreferences(USER_PREFERENCES, Context.MODE_PRIVATE)
            mails = pref.getStringSet("ass", mails) as HashSet<String>
        }

        fun getMail() = mails?.random() ?: ""

        fun addMails(mails: HashSet<String>) {
            pref.edit {
                putStringSet("ass", mails)
            }
        }
    }
}