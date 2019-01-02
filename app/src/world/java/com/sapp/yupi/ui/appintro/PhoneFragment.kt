package com.sapp.yupi.ui.appintro

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.telephony.TelephonyManager
import android.util.Patterns
import android.view.View
import android.widget.ProgressBar
import androidx.core.content.ContextCompat
import com.google.i18n.phonenumbers.NumberParseException
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.sapp.yupi.R
import com.sapp.yupi.databinding.ViewIntroPhoneBinding
import com.sapp.yupi.util.UserPrefUtil

const val PREFIX_CUBA = "+53 "

class PhoneFragment : PhoneBaseFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (mBinding as ViewIntroPhoneBinding).apply {
            textInputPhone.apply {
                setOnTouchListener { _, _ ->
                    textViewError.visibility = View.GONE
                    false
                }

                setPrefix(PREFIX_CUBA)
            }
        }
    }

    override fun setViewStateInActivationMode(enable: Boolean) {
        super.setViewStateInActivationMode(enable)

        (mBinding as ViewIntroPhoneBinding).apply {
            spinKit.visibility = if (enable) ProgressBar.GONE else ProgressBar.VISIBLE
            textInputPhone.isEnabled = enable
            textInputLayoutPhone.isEnabled = enable
        }
    }

    @SuppressLint("HardwareIds")
    override fun tryGetPhoneNumber() {
        context?.let {
            if (ContextCompat.checkSelfPermission(it, Manifest.permission.READ_PHONE_STATE) ==
                    PackageManager.PERMISSION_GRANTED) {
                (mBinding as ViewIntroPhoneBinding).apply {
                    textInputPhone.apply {
                        val text = text.toString()
                        if (text.isEmpty() || text == getPrefix()) {
                            val tMgr = context?.getSystemService(Context.TELEPHONY_SERVICE)
                                    as TelephonyManager
                            val number = tMgr.line1Number

                            if (number.isNotEmpty() && Patterns.PHONE.matcher(number).matches()) {
                                val phoneUtil = PhoneNumberUtil.getInstance()
                                try {
                                    val phoneNumber = phoneUtil.parse(number, "CU")
                                    val countryCode = phoneNumber.countryCode

                                    setPrefix("+$countryCode ")
                                    append(phoneNumber.nationalNumber.toString())
                                } catch (e: NumberParseException) {
                                    setPrefix(PREFIX_CUBA)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun validatePhone(): Boolean {
        (mBinding as ViewIntroPhoneBinding).apply {
            val phone = textInputPhone.text.toString().trim()
            val prefix = textInputPhone.getPrefix()

            errorMsgId = when {
                phone.isEmpty() -> R.string.phone_required
                prefix != PREFIX_CUBA -> R.string.install_yuupi_cuba
                !Patterns.PHONE.matcher(phone).matches() -> R.string.phone_number_not_valid
                else -> {
                    var msgIdTemp = -1
                    try {
                        val phoneUtil = PhoneNumberUtil.getInstance()
                        val phoneNumber = phoneUtil.parse(phone, "CU")
                        val countryCode = phoneNumber.countryCode

                        if (countryCode != 53) {
                            msgIdTemp = R.string.install_yuupi_cuba
                        } else if (!phoneUtil.isValidNumber(phoneNumber)) {
                            msgIdTemp = R.string.phone_number_not_valid
                        }
                    } catch (e: NumberParseException) {
                        msgIdTemp = R.string.phone_number_not_valid
                    }
                    msgIdTemp
                }
            }

            return if (errorMsgId != -1) {
                false
            } else {
                if (UserPrefUtil.getPhone() != phone) {
                    // Save to Preferences
                    UserPrefUtil.setPhone(phone)
                    isValid = false
                }

                true
            }
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
