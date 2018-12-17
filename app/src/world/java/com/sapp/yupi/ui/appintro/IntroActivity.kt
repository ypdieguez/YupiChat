package com.sapp.yupi.ui.appintro

import android.os.Bundle
import android.text.InputType
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

        addSlide(TextInputFragment.newInstance(
                fragmentTag = TAG_FRAGMENT_MAIL,
                title = R.string.intro_mail_title,
                imageRes = R.drawable.icons8_new_post_512,
                description = R.string.intro_mail_description,
                hint = R.string.intro_mail_title,
                type = InputType.TYPE_CLASS_TEXT,
                suffix = "@nauta.cu"))
        addSlide(TextInputFragment.newInstance(
                fragmentTag = TAG_FRAGMENT_MAIL_PASS,
                title = R.string.intro_pass_title,
                imageRes = R.drawable.icons8_password_480,
                description = R.string.intro_pass_description,
                hint = R.string.intro_pass_title,
                type = InputType.TYPE_TEXT_VARIATION_PASSWORD))

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
            TAG_FRAGMENT_MAIL -> {
                validateEmail(binding as ViewIntroTextInputBinding)
            }
            TAG_FRAGMENT_MAIL_PASS -> {
                validateEmailPass(binding as ViewIntroTextInputBinding)
            }
            else -> Pair(false, null)
        }
    }

    private fun validatePhone(binding: ViewIntroPhoneBinding): Pair<Boolean, Int?> {
        var error = false
        var msg: Int? = null

        binding.apply {
            val phone = textInputPhone.text.toString().trim()
            val prefix = textInputPhone.getPrefix()

            when {
                phone.isEmpty() -> {
                    error = true
                    msg = R.string.phone_required
                }
                prefix != PREFIX_CUBA -> {
                    error = true
                    msg = R.string.install_yuupi_cuba
                }
                !Patterns.PHONE.matcher(phone).matches() -> {
                    error = true
                    msg = R.string.phone_number_not_valid
                }
                else -> try {
                    val phoneUtil = PhoneNumberUtil.getInstance()
                    val phoneNumber = phoneUtil.parse(phone, "CU")
                    val countryCode = phoneNumber.countryCode

                    if (countryCode != 53) {
                        error = true
                        msg = R.string.install_yuupi_cuba
                    } else if (!phoneUtil.isValidNumber(phoneNumber)) {
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

        return Pair(error, msg)
    }

    private fun validateEmail(binding: ViewIntroTextInputBinding): Pair<Boolean, Int?> {
        var error = false
        var msg: Int? = null

        binding.apply {
            val mail = textInput.text.toString().trim()
            if (mail.isEmpty()) {
                error = true
                msg = R.string.email_required
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(mail).matches()) {
                error = true
                msg = R.string.email_not_valid

            } else if (!mail.endsWith("@nauta.cu")) {
                error = true
                msg = R.string.email_is_not_nauta
            }

            // Save to Preferences
            pref.edit {
                putString(PREF_EMAIL, mail)
            }
        }

        return Pair(error, msg)
    }

    private fun validateEmailPass(binding: ViewIntroTextInputBinding): Pair<Boolean, Int?> {
        var error = false
        var msg: Int? = null

        binding.apply {
            val pass = textInput.text.toString()
            if (pass.isEmpty()) {
                error = true
                msg = R.string.pass_required
            }
            // Save to Preferences
            pref.edit {
                putString(PREF_EMAIL_PASS, pass)
            }
        }

        return Pair(error, msg)
    }
}
