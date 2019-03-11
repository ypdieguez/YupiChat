package com.sapp.yupi.ui.appintro

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
import com.google.i18n.phonenumbers.PhoneNumberUtil.MatchType
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber
import com.sapp.yupi.R
import com.sapp.yupi.databinding.ViewIntroPhoneBinding
import com.sapp.yupi.utils.UserInfo

const val PREFIX_CUBA = "+53"

class PhoneFragment : PhoneBaseFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (mBinding as ViewIntroPhoneBinding).apply {
            textInputPhone.apply {

                setOnTouchListener { _, _ ->
                    extraFields.textViewError.visibility = View.GONE
                    false
                }

                addTextChangedListener(PhoneNumberFormattingTextWatcher("CU"))

                UserInfo.getInstance(context).apply {
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
        (mBinding as ViewIntroPhoneBinding).apply {
            extraFields.spinKit.visibility = if (enable) ProgressBar.GONE else ProgressBar.VISIBLE
            textInputPhone.isEnabled = enable
            textInputLayoutPhone.isEnabled = enable
        }
    }

    @SuppressLint("MissingPermission", "HardwareIds")
    override fun tryGetPhoneNumber() {
        context?.let {
            (mBinding as ViewIntroPhoneBinding).apply {
                textInputPhone.apply {
                    val text = text.toString()
                    if (text.isEmpty() || text == prefix) {
                        val tMgr = context?.getSystemService(Context.TELEPHONY_SERVICE)
                                as TelephonyManager
                        val number = tMgr.line1Number

                        if (number.isNotEmpty()) {
                            val phoneUtil = PhoneNumberUtil.getInstance()
                            try {
                                val phoneNumber = phoneUtil.parse(number, "CU")
                                val countryCode = phoneNumber.countryCode

                                prefix = "+$countryCode"
                                append(phoneNumber.nationalNumber.toString())
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
        (mBinding as ViewIntroPhoneBinding).apply {
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

    companion object {
        @JvmStatic
        fun newInstance() =
                PhoneFragment().apply {
                    arguments = getBundle(
                            layoutRes = R.layout.view_intro_phone,
                            fragmentTag = TAG_FRAGMENT_PHONE
                    )
                }
    }
}
