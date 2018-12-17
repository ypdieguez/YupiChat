package com.sapp.yupi.ui.appintro

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.telephony.TelephonyManager
import android.util.Patterns
import android.view.View
import androidx.core.content.ContextCompat
import com.google.i18n.phonenumbers.NumberParseException
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.sapp.yupi.R
import com.sapp.yupi.databinding.ViewIntroPhoneBinding
import com.sapp.yupi.util.UIUtils

const val PREFIX_CUBA = "+53 "

class PhoneFragment : IntroFragment() {

    private var mFirst = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (mBinding as ViewIntroPhoneBinding).apply {
            textInputPhone.apply {
                setOnTouchListener { _, _ ->
                    textInputLayoutPhone.error = null
                    false
                }

                setPrefix(PREFIX_CUBA)
            }

            textInputLayoutPhone.setErrorTextColor(ContextCompat.getColorStateList(context!!,
                    R.color.introError))
        }
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            if (mFirst) {
                mFirst = false
                if (UIUtils.askForPermission(Manifest.permission.READ_PHONE_STATE, this)) {
                    tryGetPhoneNumber()
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            tryGetPhoneNumber()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (UIUtils.checkReadPhoneStatePermission(context!!)) {
            tryGetPhoneNumber()
        }
    }

    @SuppressLint("MissingPermission", "HardwareIds")
    private fun tryGetPhoneNumber() {
        (mBinding as ViewIntroPhoneBinding).apply {
            textInputPhone.apply {
                if (text.toString() == getPrefix()) {
                    val tMgr = context?.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
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

    override fun onUserIllegallyRequestedNextPage() {
        (mBinding as ViewIntroPhoneBinding).apply {
            textInputLayoutPhone.error = getString(mError.second!!)
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
