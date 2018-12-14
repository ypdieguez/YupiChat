package com.sapp.yupi.ui.appintro


import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.telephony.TelephonyManager
import android.util.Patterns
import android.view.View
import androidx.core.content.ContextCompat
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.google.i18n.phonenumbers.geocoding.PhoneNumberOfflineGeocoder
import com.sapp.yupi.R
import com.sapp.yupi.databinding.ViewIntroPhoneBinding
import com.sapp.yupi.util.UIUtils
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import net.yslibrary.android.keyboardvisibilityevent.Unregistrar
import java.util.*


class PhoneFragment : IntroFragment(), CountryListDialogFragment.Listener {

    private var mFirst = true
    private var mCountryViewTouched = false
    private var unregistrar: Unregistrar? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (mBinding as ViewIntroPhoneBinding).apply {
            textInputPhone.apply {
                setOnTouchListener { _, _ ->
                    textInputLayoutPhone.error = null
                    false
                }
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
                if (UIUtils.askForPermission(activity!!, Manifest.permission.READ_PHONE_STATE,
                                this)) {
                    tryGetPhoneNumber()
                }
            }
        } else {
            unregistrar?.unregister()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            tryGetPhoneNumber()
        }
    }

    @SuppressLint("MissingPermission", "HardwareIds")
    private fun tryGetPhoneNumber() {
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

    override fun onUserIllegallyRequestedNextPage() {
        (mBinding as ViewIntroPhoneBinding).apply {
            if (mError.second!! == R.string.country_not_supported) {
                val text = textInputCountry.text.toString()
                textInputLayoutCountry.apply {
                    error = when (text) {
                        getString(R.string.choose_country) -> getString(R.string.choose_country)
                        "Cuba" -> getString(R.string.chosse_yuupi_cuba)
                        else -> getString(mError.second!!)
                    }
                }

                textInputLayoutPhone.error = null
            } else {
                textInputLayoutCountry.error = null
                textInputLayoutPhone.error = getString(mError.second!!)
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
