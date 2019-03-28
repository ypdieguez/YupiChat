package com.sapp.yupi.ui.appintro.world

import android.os.Bundle
import android.text.InputType
import android.text.SpannableStringBuilder
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.widget.ProgressBar
import com.google.i18n.phonenumbers.NumberParseException
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.sapp.yupi.R
import com.sapp.yupi.UserPref
import com.sapp.yupi.databinding.ViewIntroPhoneWorldBinding
import com.sapp.yupi.ui.appintro.PhoneBaseFragment
import com.sapp.yupi.ui.appintro.PhoneNumberFormattingTextWatcher
import com.sapp.yupi.ui.appintro.TAG_FRAGMENT_PHONE
import com.sapp.yupi.ui.appintro.data.Country
import com.sapp.yupi.utils.STATUS_AUTHENTICATION_FAILED_EXCEPTION
import com.sapp.yupi.utils.STATUS_MAIL_CONNECT_EXCEPTION
import com.sapp.yupi.utils.STATUS_OTHER_EXCEPTION
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import net.yslibrary.android.keyboardvisibilityevent.Unregistrar
import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil
import java.util.*

class PhoneFragment : PhoneBaseFragment(), CountryListDialogFragment.Listener {

    private var mCountryViewTouched = false
    private var mUnregistrar: Unregistrar? = null

    private var mWatcher = PhoneNumberFormattingTextWatcher()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (mBinding as ViewIntroPhoneWorldBinding).apply {
            textInputCountry.apply {
                inputType = InputType.TYPE_NULL

                setOnTouchListener { _, event ->
                    if (event.action == KeyEvent.ACTION_UP) {
                        extraFields.textViewError.visibility = View.GONE

                        if (KeyboardVisibilityEvent.isKeyboardVisible(activity)) {
                            // This code do next:
                            // 1. Hide Soft keyboard
                            // 2. Execute the listener registered in setUserVisibleHint
                            // This code is for harmony when hiding the keyboard and showing the dialogue.
                            mCountryViewTouched = true
                            UIUtil.hideKeyboard(context, this)
                        } else {
                            CountryListDialogFragment.newInstance()
                                    .addListener(this@PhoneFragment)
                                    .show(requireFragmentManager(), "dialog")
                        }
                    }
                    false
                }
            }

            textInputPhone.apply {
                setOnTouchListener { _, event ->
                    if (event.action == KeyEvent.ACTION_DOWN) {
                        textInputCountry.text.toString().let { country ->
                            errorMsgId = if (country.isEmpty()) {
                                R.string.choose_country
                            } else if (country == "Cuba" || prefix == "+53") {
                                R.string.choose_yuupi_world
                            } else {
                                -1
                            }

                            val flag = errorMsgId != -1
                            showError(flag)
                            return@setOnTouchListener flag
                        }
                    }
                    return@setOnTouchListener false
                }

                addTextChangedListener(mWatcher)
            }
        }

        fillFields(UserPref.getInstance(requireContext()).phone)
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            // Code added to avoid exception:
            // Parameter:activity window SoftInputMethod is not ADJUST_RESIZE
            activity!!.window.attributes.softInputMode =
                    WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE

            // This listener is for harmony when hiding the keyboard and showing the dialogue.
            mUnregistrar = KeyboardVisibilityEvent
                    .registerEventListener(activity!!) { isOpen ->
                        if (!isOpen && mCountryViewTouched) {
                            CountryListDialogFragment.newInstance()
                                    .addListener(this@PhoneFragment)
                                    .show(requireFragmentManager(), "dialog")
                            mCountryViewTouched = false
                        }
                    }
        } else {
            mUnregistrar?.unregister()
        }
    }

    override fun tryGetPhoneNumber() {
        (mBinding as ViewIntroPhoneWorldBinding).apply {
            val country = textInputCountry.text?.toString() ?: ""
            val number = textInputPhone.text?.toString() ?: ""
            if (country.isEmpty() && number.isEmpty()) {
                val locale = Locale.getDefault()
                val phoneNumber = getNumber(locale.country)

                // Update Text Watcher always first
                updateTextWatcher(phoneNumber.regionCode)
                textInputCountry.setText(Locale(locale.language, phoneNumber.regionCode).displayCountry)
                textInputPhone.prefix = "+${phoneNumber.countryCode}"
                textInputPhone.append(phoneNumber.number)
            }
        }
    }

    override fun onCountryClicked(country: Country) {
        (mBinding as ViewIntroPhoneWorldBinding).apply {
            textInputCountry.text = SpannableStringBuilder(country.name)
            textInputPhone.prefix = "+${country.code}"

            updateTextWatcher(country.region)
        }
    }

    override fun setViewStateInActivationMode(enable: Boolean) {
        super.setViewStateInActivationMode(enable)
        (mBinding as ViewIntroPhoneWorldBinding).apply {
            extraFields.spinKit.visibility = if (enable) ProgressBar.GONE else ProgressBar.VISIBLE
            textInputCountry.isEnabled = enable
            textInputLayoutCountry.isEnabled = enable
            textInputPhone.isEnabled = enable
            textInputLayoutPhone.isEnabled = enable
        }
    }

    override fun isValidPhone(): Boolean {
        (mBinding as ViewIntroPhoneWorldBinding).apply {
            val country = textInputCountry.text.toString()
            val phone = textInputPhone.text.toString().trim()

            errorMsgId = when {
                country.isEmpty() -> R.string.choose_country
                country == getString(R.string.choose_country) -> R.string.choose_country
                phone.isEmpty() -> R.string.phone_required
                else -> try {
                    val phoneUtil = PhoneNumberUtil.getInstance()
                    val phoneNumber = phoneUtil.parse(phone, null)

                    if (phoneNumber.countryCode == 53) {
                        R.string.choose_yuupi_world
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

            return if (errorMsgId != -1) {
                isValidated = false
                false
            } else {
                true
            }
        }
    }

    override fun checkSentVerificationEmail(result: Byte): Boolean {
        (mBinding as ViewIntroPhoneWorldBinding).apply {
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
        (mBinding as ViewIntroPhoneWorldBinding).extraFields.apply {
            if (show) {
                textViewError.setText(errorMsgId)
                textViewError.visibility = View.VISIBLE
            } else {
                textViewError.text = ""
                textViewError.visibility = View.GONE
            }
        }
    }

    private fun fillFields(number: String): Boolean {
        if (number.isNotEmpty()) {
            (mBinding as ViewIntroPhoneWorldBinding).apply {
                return try {
                    val phoneUtil = PhoneNumberUtil.getInstance()
                    val phoneNumber = phoneUtil.parse(number, null)

                    val countryCode = phoneNumber.countryCode
                    val regionCode = phoneUtil.getRegionCodeForNumber(phoneNumber)
                            ?: phoneUtil.getRegionCodeForCountryCode(countryCode)

                    val country = Locale(Locale.getDefault().language, regionCode).displayCountry

                    // Update Text Watcher always first
                    updateTextWatcher(regionCode)
                    // Update EditText
                    textInputCountry.setText(country)
                    textInputPhone.apply {
                        prefix = "+$countryCode"
                        text = SpannableStringBuilder("+$countryCode${phoneNumber.nationalNumber}")
                    }
                    true
                } catch (e: NumberParseException) {
                    false
                } catch (e: Exception) {
                    false
                }
            }
        }
        return false
    }

    private fun updateTextWatcher(iso: String) {
        mWatcher.refresh(iso)
    }

    companion object {
        @JvmStatic
        fun newInstance() =
                PhoneFragment().apply {
                    arguments = getBundle(
                            layoutRes = R.layout.view_intro_phone_world,
                            fragmentTag = TAG_FRAGMENT_PHONE
                    )
                }
    }
}