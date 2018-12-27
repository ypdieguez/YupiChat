package com.sapp.yupi.ui.appintro

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.telephony.TelephonyManager
import android.text.InputType
import android.util.Patterns
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.widget.ProgressBar
import com.google.i18n.phonenumbers.NumberParseException
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.google.i18n.phonenumbers.geocoding.PhoneNumberOfflineGeocoder
import com.sapp.yupi.R
import com.sapp.yupi.databinding.ViewIntroPhoneBinding
import com.sapp.yupi.util.UserPrefUtil
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import net.yslibrary.android.keyboardvisibilityevent.Unregistrar
import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil
import java.util.*


class PhoneFragment : PhoneBaseFragment(), CountryListDialogFragment.Listener {

    private var mCountryViewTouched = false
    private var unregistrar: Unregistrar? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (mBinding as ViewIntroPhoneBinding).apply {
            textInputCountry.apply {
                inputType = InputType.TYPE_NULL

                setOnTouchListener { _, event ->
                    if (event.action == KeyEvent.ACTION_UP) {
                        textViewError.visibility = View.GONE

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
                                    .show(fragmentManager, "dialog")
                        }
                    }
                    false
                }
            }

            textInputPhone.apply {
                setOnTouchListener { _, event ->
                    if (event.action == KeyEvent.ACTION_DOWN) {
                        textInputCountry.text.apply {
                            if (toString() == "Cuba" || getPrefix() == "+53") {
                                textViewError.text = getString(R.string.chosse_yuupi_cuba)
                                textViewError.visibility = View.VISIBLE
                                return@setOnTouchListener true
                            } else if (!resources.getStringArray(R.array.countries_name)
                                            .contains(toString())) {
                                textViewError.text = getString(R.string.choose_country)
                                textViewError.visibility = View.VISIBLE
                                return@setOnTouchListener true
                            } else {
                                textViewError.visibility = View.GONE
                            }
                        }
                    }
                    return@setOnTouchListener false
                }
            }
        }
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            if (isFirstTime) {
                checkPermissions()
                isFirstTime = false
            }

            // Code added to avoid exception:
            // Parameter:activity window SoftInputMethod is not ADJUST_RESIZE
            activity!!.window.attributes.softInputMode =
                    WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE

            // This listener is for harmony when hiding the keyboard and showing the dialogue.
            unregistrar = KeyboardVisibilityEvent
                    .registerEventListener(activity!!) { isOpen ->
                        if (!isOpen && mCountryViewTouched) {
                            CountryListDialogFragment.newInstance()
                                    .addListener(this@PhoneFragment)
                                    .show(fragmentManager, "dialog")
                            mCountryViewTouched = false
                        }
                    }
        } else {
            unregistrar?.unregister()
        }
    }

    @SuppressLint("MissingPermission", "HardwareIds")
    override fun tryGetPhoneNumber() {
        (mBinding as ViewIntroPhoneBinding).apply {
            var country = textInputCountry.text?.toString() ?: ""
            var number = textInputPhone.text?.toString() ?: ""
            if (country.isEmpty() || country == getString(R.string.choose_country) && number.isEmpty()) {
                val tMgr = context?.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                number = tMgr.line1Number
                val networkCountryIso = tMgr.networkCountryIso
                val simCountryIso = tMgr.simCountryIso

                if (number.isNotEmpty() && Patterns.PHONE.matcher(number).matches()) {
                    val phoneUtil = PhoneNumberUtil.getInstance()
                    val geocoder = PhoneNumberOfflineGeocoder.getInstance()
                    val phoneNumber = phoneUtil.parse(number, null)

                    country = geocoder.getDescriptionForNumber(phoneNumber, Locale.getDefault())
                    val countryCode = phoneNumber.countryCode

                    textInputCountry.setText(country)
                    textInputPhone.setPrefix("+$countryCode ")
                    textInputPhone.append(phoneNumber.nationalNumber.toString())

                } else if (networkCountryIso.isNotEmpty() || simCountryIso.isNotEmpty()) {
                    val iso = (networkCountryIso ?: simCountryIso).toUpperCase()
                    val phoneUtil = PhoneNumberUtil.getInstance()

                    country = Locale(Locale.getDefault().language, iso).displayCountry
                    val countryCode = phoneUtil.getCountryCodeForRegion(iso)

                    textInputCountry.setText(country)
                    textInputPhone.setPrefix("+$countryCode ")
                    textInputPhone.setText("")
                } else {
                    val phoneUtil = PhoneNumberUtil.getInstance()
                    val locale = Locale.getDefault()

                    country = locale.displayCountry
                    textInputCountry.setText(country)

                    val iso = locale.country.toUpperCase()
                    if (iso.isNotEmpty()) {
                        val countryCode = phoneUtil.getCountryCodeForRegion(iso)
                        textInputPhone.setPrefix("+$countryCode ")
                        textInputPhone.setText("")
                    }
                }
            }
        }
    }

    override fun onCountryClicked(position: Int) {
        (mBinding as ViewIntroPhoneBinding).apply {
            val country = resources.getStringArray(R.array.countries_name)[position]
            textInputCountry.setText(country)

            val iso = resources.getStringArray(R.array.countries_iso)[position]
            val phoneUtil = PhoneNumberUtil.getInstance()
            val countryCode = phoneUtil.getCountryCodeForRegion(iso)
            textInputPhone.setPrefix("+$countryCode ")
        }
    }

    override fun setViewStateInActivationMode(enable: Boolean) {
        (mBinding as ViewIntroPhoneBinding).apply {
            spinKit.visibility = if (enable) ProgressBar.GONE else ProgressBar.VISIBLE
            textInputCountry.isEnabled = enable
            textInputLayoutCountry.isEnabled = enable
            textInputPhone.isEnabled = enable
            textInputLayoutPhone.isEnabled = enable
        }
    }

    override fun validatePhone(): Boolean {
        (mBinding as ViewIntroPhoneBinding).apply {
            val validCountries = resources.getStringArray(R.array.countries_name)
            val countriesIso = resources.getStringArray(R.array.countries_iso)

            val country = textInputCountry.text.toString()
            val index = validCountries.indexOf(country)
            val phone = textInputPhone.text.toString().trim()

            errorMsgId = when {
                country.isEmpty() -> R.string.choose_country
                country == getString(R.string.choose_country) -> R.string.choose_country
                country == "Cuba" -> R.string.chosse_yuupi_cuba
                index == -1 -> R.string.country_not_supported
                phone.isEmpty() -> R.string.phone_required
                !Patterns.PHONE.matcher(phone).matches() -> R.string.phone_number_not_valid
                else -> try {
                    val phoneUtil = PhoneNumberUtil.getInstance()
                    val phoneNumber = phoneUtil.parse(phone, countriesIso[index])

                    if (!phoneUtil.isValidNumber(phoneNumber)) {
                        R.string.phone_number_not_valid
                    } else {
                        if (UserPrefUtil.getPhone() != phone) {
                            // Save to Preferences
                            UserPrefUtil.setPhone(phone)
                            isValid = false
                        }
                        -1
                    }
                } catch (e: NumberParseException) {
                    R.string.phone_number_not_valid
                }
            }
        }

        return errorMsgId == -1
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