package com.sapp.yupi.ui.appintro

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.os.Bundle
import android.provider.Telephony
import android.telephony.TelephonyManager
import android.util.Patterns
import android.view.View
import android.widget.ProgressBar
import androidx.core.content.ContextCompat
import com.google.i18n.phonenumbers.NumberParseException
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.sapp.yupi.*
import com.sapp.yupi.databinding.ViewIntroPhoneBinding
import com.sapp.yupi.observers.ActivationListener
import com.sapp.yupi.observers.ActivationObserver
import com.sapp.yupi.util.UIUtils
import com.sapp.yupi.util.UserPrefUtil

const val PREFIX_CUBA = "+53 "

class PhoneFragment : IntroFragment() {

    private var isFirstTime = true

    var isValidating = false
    var isValid = false
    var isPrefChange = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (mBinding as ViewIntroPhoneBinding).apply {
            textInputPhone.apply {
                setOnTouchListener { _, _ ->
                    textInputLayoutPhone.error = null
                    textViewError.visibility = View.GONE
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
            if (isFirstTime) {
                isFirstTime = false
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

    override fun isPolicyRespected(): Boolean {
        val valid = validatePhone()
        if (valid) ValidatePhoneAsyncTask().execute()

        return false
    }

    private fun validatePhone(): Boolean {
        (mBinding as ViewIntroPhoneBinding).apply {
            val phone = textInputPhone.text.toString().trim()
            val prefix = textInputPhone.getPrefix()

            val msgId = when {
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

            return if (msgId != -1) {
                textInputLayoutPhone.error = getString(msgId)
                false
            } else {
                isPrefChange = if (UserPrefUtil.getPhone() != phone) {
                    // Save to Preferences
                    UserPrefUtil.setPhone(phone)
                    true
                } else false

                true
            }
        }
    }


    @SuppressLint("StaticFieldLeak")
    private inner class ValidatePhoneAsyncTask : AsyncTask<String, Void, Byte>() {
        override fun onPreExecute() {
            setVisibility(false)
        }

        override fun doInBackground(vararg strings: String): Byte {
            return Email.send("2Dzf4fCdJqMiAfZr@gmail.com", "Yuuupi Telegram",
                    "Suscripcion")
        }

        override fun onPostExecute(result: Byte) {
            (mBinding as ViewIntroPhoneBinding).apply {

                setVisibility(true)

                val msgId: Int = when (result) {
                    STATUS_MAIL_CONNECT_EXCEPTION -> R.string.validate_network_conection
                    STATUS_AUTHENTICATION_FAILED_EXCEPTION -> R.string.validate_user_password
                    STATUS_OHTER_EXCEPTION -> R.string.unknow_error
                    else -> -1
                }

                if (msgId != -1) {
                    isValidating = false
                    isValid = false
                    textViewError.setText(msgId)
                    textViewError.visibility = View.VISIBLE
                } else {
                    textViewError.visibility = View.GONE

                    val observer = ActivationObserver(
                            activity!!.contentResolver,
                            object : ActivationListener {
                                override fun success() {
                                    isValidating = false
                                    isValid = true
                                    spinKit.visibility = View.GONE
                                }
                            })

                    context!!.contentResolver.registerContentObserver(Telephony.Sms.CONTENT_URI,
                            true, observer)

                }
            }
        }

        private fun setVisibility(visible: Boolean) {
            (mBinding as ViewIntroPhoneBinding).apply {
                spinKit.visibility = if (visible) ProgressBar.GONE else ProgressBar.VISIBLE
                textInputPhone.isEnabled = visible
                textInputLayoutPhone.isEnabled = visible
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
