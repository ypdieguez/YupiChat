package com.sapp.yupi.utils

import android.util.Patterns
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat
import java.lang.NumberFormatException

class PhoneUtil {
    companion object {
        fun toInternational(number: String, defaultRegion: String?): String = try {
            PhoneNumberUtil.getInstance().let {
                it.format(it.parse(number, defaultRegion), PhoneNumberFormat.INTERNATIONAL)
            }
        } catch (e: NumberFormatException) {
            number
        }

        fun toE164(number: String, defaultRegion: String?): String = try {
            PhoneNumberUtil.getInstance().let {
                it.format(it.parse(number, defaultRegion), PhoneNumberFormat.E164)
            }
        } catch (e: NumberFormatException) {
            number
        }

        /**
         * Is the specified number a phone number?
         *
         * @param number the input number to test
         * @return true if number is a phone number; false otherwise.
         */
        fun isPhoneNumber(number: String): Boolean {
            val match = Patterns.PHONE.matcher(number)
            return match.matches()
        }

    }
}