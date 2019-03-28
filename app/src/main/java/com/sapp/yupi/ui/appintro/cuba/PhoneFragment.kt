package com.sapp.yupi.ui.appintro.cuba

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.util.Patterns
import android.view.View
import android.widget.ProgressBar
import com.google.i18n.phonenumbers.NumberParseException
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.sapp.yupi.R
import com.sapp.yupi.UserPref
import com.sapp.yupi.databinding.ViewIntroPhoneCubaBinding
import com.sapp.yupi.ui.appintro.PhoneBaseFragment
import com.sapp.yupi.ui.appintro.PhoneNumberFormattingTextWatcher
import com.sapp.yupi.ui.appintro.TAG_FRAGMENT_PHONE
import com.sapp.yupi.utils.STATUS_AUTHENTICATION_FAILED_EXCEPTION
import com.sapp.yupi.utils.STATUS_MAIL_CONNECT_EXCEPTION
import com.sapp.yupi.utils.STATUS_OTHER_EXCEPTION

const val PREFIX_CUBA = "+53"

class PhoneFragment : PhoneBaseFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (mBinding as ViewIntroPhoneCubaBinding).apply {
            textInputPhone.apply {

                setOnTouchListener { _, _ ->
                    extraFields.textViewError.visibility = View.GONE
                    false
                }

                addTextChangedListener(PhoneNumberFormattingTextWatcher("CU"))

                UserPref.getInstance(context).apply {
                    if (phone.isNotEmpty()) {
                        prefix = PREFIX_CUBA
                        text = SpannableStringBuilder(phone)
                    }
                }
            }
        }
    }

    override fun setViewStateInActivationMode(enable: Boolean) {
        super.setViewStateInActivationMode(enable)
        (mBinding as ViewIntroPhoneCubaBinding).apply {
            extraFields.spinKit.visibility = if (enable) ProgressBar.GONE else ProgressBar.VISIBLE
            textInputPhone.isEnabled = enable
            textInputLayoutPhone.isEnabled = enable
        }
    }

    override fun tryGetPhoneNumber() {
        context?.let {
            (mBinding as ViewIntroPhoneCubaBinding).apply {
                textInputPhone.apply {
                    val phone = text.toString()
                    if (phone.isEmpty()) {
                        val phoneNumber = getNumber("CU")
                        // Update Text Watcher always first
                        addTextChangedListener(PhoneNumberFormattingTextWatcher(phoneNumber.regionCode))
                        prefix = "+${phoneNumber.countryCode}"
                        append(phoneNumber.number)
                    }
                }
            }
        }
    }

    override fun isValidPhone(): Boolean {
        (mBinding as ViewIntroPhoneCubaBinding).apply {
            val number = textInputPhone.text.toString().trim()

            errorMsgId = when {
                number.isEmpty() -> R.string.phone_required
                !Patterns.PHONE.matcher(number).matches() -> R.string.phone_number_not_valid
                else -> {
                    try {
                        val phoneUtil = PhoneNumberUtil.getInstance()
                        val phoneNumber = phoneUtil.parse(number, "CU")
                        val countryCode = phoneNumber.countryCode

                        if (countryCode != 53) {
                            R.string.choose_yuupi_cuba
                        } else if (!phoneUtil.isValidNumber(phoneNumber)) {
                            R.string.phone_number_not_valid
                        } else {
                            updateUserInfo(phoneNumber)
                            -1
                        }
                    } catch (e: NumberParseException) {
                        R.string.phone_number_not_valid
                    }
                }
            }

            return if (errorMsgId != -1) {
                isValidated = false
                false
            } else {
                true
            }
        }
    }

    override fun checkSentVerificationEmail(result: Byte): Boolean {
        (mBinding as ViewIntroPhoneCubaBinding).apply {
            val msgId: Int = when (result) {
                STATUS_MAIL_CONNECT_EXCEPTION -> R.string.host_not_connected_world
                STATUS_AUTHENTICATION_FAILED_EXCEPTION -> R.string.wrong_user_or_password
                STATUS_OTHER_EXCEPTION -> R.string.unknown_error
                else -> -1
            }

            return if (msgId != -1) {
                isValidating = false
                isValidated = false
                extraFields.apply {
                    textViewError.setText(msgId)
                    textViewError.visibility = View.VISIBLE
                }
                setViewStateInActivationMode(true)

                false
            } else {
                extraFields.textViewError.visibility = View.GONE

                true
            }
        }
    }

    override fun showError(show: Boolean) {
        (mBinding as ViewIntroPhoneCubaBinding).extraFields.apply {
            if (show) {
                textViewError.setText(errorMsgId)
                textViewError.visibility = View.VISIBLE
            } else {
                textViewError.text = ""
                textViewError.visibility = View.GONE
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
                PhoneFragment().apply {
                    arguments = getBundle(
                            layoutRes = R.layout.view_intro_phone_cuba,
                            fragmentTag = TAG_FRAGMENT_PHONE
                    )
                }
    }
}
