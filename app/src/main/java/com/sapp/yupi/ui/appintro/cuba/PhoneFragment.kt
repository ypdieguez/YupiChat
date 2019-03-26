package com.sapp.yupi.ui.appintro.cuba

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.telephony.TelephonyManager
import android.text.SpannableStringBuilder
import android.util.Patterns
import android.view.View
import android.widget.ProgressBar
import com.google.i18n.phonenumbers.NumberParseException
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.sapp.yupi.R
import com.sapp.yupi.databinding.ViewIntroPhoneCubaBinding
import com.sapp.yupi.ui.appintro.PhoneBaseFragment
import com.sapp.yupi.ui.appintro.PhoneNumberFormattingTextWatcher
import com.sapp.yupi.ui.appintro.TAG_FRAGMENT_PHONE
import com.sapp.yupi.utils.STATUS_AUTHENTICATION_FAILED_EXCEPTION
import com.sapp.yupi.utils.STATUS_MAIL_CONNECT_EXCEPTION
import com.sapp.yupi.utils.STATUS_OTHER_EXCEPTION
import com.sapp.yupi.UserPref

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

    @SuppressLint("MissingPermission", "HardwareIds")
    override fun tryGetPhoneNumber() {
        context?.let {
            (mBinding as ViewIntroPhoneCubaBinding).apply {
                textInputPhone.apply {
                    val phone = text.toString()
                    if (phone.isEmpty() || phone == prefix) {
                        val tMgr = context?.getSystemService(Context.TELEPHONY_SERVICE)
                                as TelephonyManager
                        val number = tMgr.line1Number

                        if (number.isNotEmpty()) {
                            val phoneUtil = PhoneNumberUtil.getInstance()
                            try {
                                val phoneNumber = phoneUtil.parse(number, "CU")
                                val countryCode = phoneNumber.countryCode

                                prefix = "+$countryCode"
                                text = SpannableStringBuilder(phoneNumber.nationalNumber.toString())
                            } catch (e: NumberParseException) {
                                prefix = PREFIX_CUBA
                            }
                        }
                    }
                }
            }
        }
    }

    override fun isValidPhone(): Boolean {
        (mBinding as ViewIntroPhoneCubaBinding).apply {
            val number = textInputPhone.text.toString().trim()
            val prefix = textInputPhone.prefix

            errorMsgId = when {
                number.isEmpty() -> R.string.phone_required
                prefix != PREFIX_CUBA -> R.string.install_yuupi_cuba
                !Patterns.PHONE.matcher(number).matches() -> R.string.phone_number_not_valid
                else -> {
                    try {
                        val phoneUtil = PhoneNumberUtil.getInstance()
                        val phoneNumber = phoneUtil.parse(number, "CU")
                        val countryCode = phoneNumber.countryCode

                        if (countryCode != 53) {
                            R.string.install_yuupi_cuba
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

            return errorMsgId == -1
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
