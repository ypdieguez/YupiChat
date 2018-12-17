package com.sapp.yupi.ui.appintro

import android.os.Bundle
import android.util.Patterns
import androidx.core.content.edit
import androidx.databinding.ViewDataBinding
import com.google.i18n.phonenumbers.NumberParseException
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.sapp.yupi.R
import com.sapp.yupi.databinding.ViewIntroPhoneBinding
import com.sapp.yupi.databinding.ViewIntroTextInputBinding

class IntroActivity : IntroBaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Add slides
        commonSlidesStart()
        addSlide(PhoneFragment.newInstance())
        commonSlidesEnd()
    }

    override fun validate(binding: ViewDataBinding, tag: String): Pair<Boolean, Int?> {
        return when (tag) {
            TAG_FRAGMENT_NAME -> {
                validateName(binding as ViewIntroTextInputBinding)
            }
            TAG_FRAGMENT_PHONE -> {
                validatePhone(binding as ViewIntroPhoneBinding)
            }
            else -> Pair(false, null)
        }
    }

    private fun validatePhone(binding: ViewIntroPhoneBinding): Pair<Boolean, Int?> {
        var error = false
        var msg: Int? = null

        binding.apply {

            val validCountries = resources.getStringArray(R.array.countries_name)
            val countriesIso = resources.getStringArray(R.array.countries_iso)

            val country = textInputCountry.text.toString()

            val index = validCountries.indexOf(country)
            if (index == -1) {
                error = true
                msg = R.string.country_not_supported
            } else {
                val phone = textInputPhone.text.toString().trim()
                when {
                    phone.isEmpty() -> {
                        error = true
                        msg = R.string.phone_required
                    }
                    !Patterns.PHONE.matcher(phone).matches() -> {
                        error = true
                        msg = R.string.phone_number_not_valid
                    }
                    else -> try {
                        val phoneUtil = PhoneNumberUtil.getInstance()

                        val phoneNumber = phoneUtil.parse(phone, countriesIso[index])

                        if (!phoneUtil.isValidNumber(phoneNumber)) {
                            error = true
                            msg = R.string.phone_number_not_valid
                        }
                    } catch (e: NumberParseException) {
                        error = true
                        msg = R.string.phone_number_not_valid
                    }
                }

                // Save to Preferences
                pref.edit {
                    putString(PREF_PHONE, phone)
                }
            }
        }

        return Pair(error, msg)
    }
}
