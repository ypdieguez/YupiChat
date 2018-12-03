package com.sapp.yupi.ui.appintro

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.InputType
import android.util.Patterns
import android.widget.ImageButton
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.github.paolorotolo.appintro.AppIntro
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.sapp.yupi.BuildConfig
import com.sapp.yupi.R
import com.sapp.yupi.databinding.ViewIntroPhoneBinding
import com.sapp.yupi.databinding.ViewIntroTextInputBinding
import com.sapp.yupi.ui.FIRST_LAUNCH
import com.sapp.yupi.ui.MainActivity
import com.sapp.yupi.util.UIUtils


const val USER_PREFERENCES = "user_preferences"

const val PREF_PHONE = "phone"
const val PREF_EMAIL = "email"
const val PREF_EMAIL_PASS = "email_pass"
const val PREF_NAME = "name"

const val TAG_FRAGMENT_PHONE = "fragment_phone"
const val TAG_FRAGMENT_MAIL = "fragment_mail"
const val TAG_FRAGMENT_MAIL_PASS = "fragment_mail_pass"
const val TAG_FRAGMENT_NAME = "fragment_name"

class IntroActivity : AppIntro(), IntroFragment.PolicyListener {

    private lateinit var pref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Enable Wizard mode
        wizardMode = true

        // Initialize preferences
        pref = getSharedPreferences(USER_PREFERENCES, Context.MODE_PRIVATE)

        // Add slides
        addSlide(PresentationFragment.newInstance(
                title = R.string.app_name,
                imageRes = R.drawable.icons8_sms_384,
                description = R.string.intro_app_description))
        addSlide(PresentationFragment.newInstance(
                title = R.string.intro_config_anounce_title,
                imageRes = R.drawable.icons8_phonelink_setup_512,
                description = R.string.intro_config_anounce_description))
        addSlide(TextInputFragment.newInstance(
                fragmentTag = TAG_FRAGMENT_NAME,
                title = R.string.name,
                imageRes = R.drawable.icons8_customer_480,
                description = R.string.intro_name_description,
                hint = R.string.name,
                type = InputType.TYPE_TEXT_VARIATION_PERSON_NAME or InputType.TYPE_TEXT_FLAG_CAP_WORDS)
        )
        addSlide(PhoneFragment.newInstance())

        if (BuildConfig.FLAVOR == BuildConfig.FLAVOR_WORLD) {
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
        }

        if (!UIUtils.checkPermission(this)) {
            addSlide(ReadSmsPermissionFragment())
        }

        addSlide(PresentationFragment.newInstance(
                title = R.string.intro_conclusion_title,
                imageRes = R.drawable.icons8_confetti_512,
                description = R.string.intro_conclusion_description))

        // Define colors
        val colorPrimary = ContextCompat.getColor(this, R.color.colorPrimary)
        val coloBackground = ContextCompat.getColor(this, R.color.background)

        // Set properties
        skipButtonEnabled = false
        backButtonVisibilityWithDone = true

        setIndicatorColor(colorPrimary, colorPrimary)
        setNextArrowColor(colorPrimary)
        setColorDoneText(colorPrimary)
        setSeparatorColor(colorPrimary)
        setColorSkipButton(colorPrimary)
        (backButton as ImageButton).setColorFilter(colorPrimary)
        setBarColor(coloBackground)

        setScrollDurationFactor(2)
        setSwipeLock(true)
    }

    override fun onDonePressed(currentFragment: Fragment?) {
        pref.edit {
            putBoolean(FIRST_LAUNCH, false)
        }
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    override fun validate(binding: ViewDataBinding, tag: String): Pair<Boolean, String?> {
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

    private fun validateName(binding: ViewIntroTextInputBinding): Pair<Boolean, String?> {
        var error = false
        var msg: String? = null

        binding.apply {
            val name = textInput.text.toString().trim()
            if (name.isEmpty()) {
                error = true
                msg = getString(R.string.name_required)
            }
            // Save to Preferences
            pref.edit {
                putString(PREF_NAME, name)
            }
        }

        return Pair(error, msg)
    }

    private fun validatePhone(binding: ViewIntroPhoneBinding): Pair<Boolean, String?> {
        var error = false
        var msg: String? = null

        binding.apply {

            val validCountries = resources.getStringArray(R.array.countries_name)
            val country = textInputCountry.text.toString()
            if (!validCountries.contains(country)) {
                error = true
                msg = getString(R.string.country_not_supported)
            } else {

                val phone = textInputPhone.text.toString().trim()
                if (phone.isEmpty()) {
                    error = true
                    msg = getString(R.string.phone_required)
                } else if (!Patterns.PHONE.matcher(phone).matches()) {
                    error = true
                    msg = getString(R.string.phone_not_valid)

                } else {
                    val phoneUtil = PhoneNumberUtil.getInstance()
                    val phoneNumber = phoneUtil.parse(phone, null)
                    val isValid = phoneUtil.isValidNumber(phoneNumber)

                    if (!isValid) {
                        error = true
                        msg = getString(R.string.phone_no_valid_region)
                    }
                }

                // Save to Preferences
                pref.edit {
                    putString(PREF_PHONE, phone)
                }
            }
        }

        return Pair(error, msg)
    }

    private fun validateEmail(binding: ViewIntroTextInputBinding): Pair<Boolean, String?> {
        var error = false
        var msg: String? = null

        binding.apply {
            val mail = textInput.text.toString().trim()
            if (mail.isEmpty()) {
                error = true
                msg = getString(R.string.email_required)
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(mail).matches()) {
                error = true
                msg = getString(R.string.email_not_valid)

            } else if (!mail.endsWith("@nauta.cu")) {
                error = true
                msg = getString(R.string.email_is_not_nauta)
            }

            // Save to Preferences
            pref.edit {
                putString(PREF_EMAIL, mail)
            }
        }

        return Pair(error, msg)
    }

    private fun validateEmailPass(binding: ViewIntroTextInputBinding): Pair<Boolean, String?> {
        var error = false
        var msg: String? = null

        binding.apply {
            val pass = textInput.text.toString()
            if (pass.isEmpty()) {
                error = true
                msg = getString(R.string.pass_required)
            }
            // Save to Preferences
            pref.edit {
                putString(PREF_EMAIL_PASS, pass)
            }
        }

        return Pair(error, msg)
    }
}
