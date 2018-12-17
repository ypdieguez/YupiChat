package com.sapp.yupi.ui.appintro

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.InputType
import android.widget.ImageButton
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import com.github.paolorotolo.appintro.AppIntro
import com.sapp.yupi.R
import com.sapp.yupi.databinding.ViewIntroTextInputBinding
import com.sapp.yupi.ui.FIRST_LAUNCH
import com.sapp.yupi.ui.MainActivity
import com.sapp.yupi.util.UIUtils


const val USER_PREFERENCES = "user_preferences"

const val PREF_PHONE = "phone"
const val PREF_EMAIL = "email"
const val PREF_EMAIL_PASS = "email_pass"
const val PREF_NAME = "name"

const val TAG_FRAGMENT_NAME = "fragment_name"
const val TAG_FRAGMENT_PHONE = "fragment_phone"
const val TAG_FRAGMENT_MAIL = "fragment_mail"
const val TAG_FRAGMENT_MAIL_PASS = "fragment_mail_pass"

abstract class IntroBaseActivity : AppIntro(), IntroFragment.PolicyListener {

    protected lateinit var pref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize preferences
        pref = getSharedPreferences(USER_PREFERENCES, Context.MODE_PRIVATE)

        //Enable Wizard mode
        wizardMode = true

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

    protected fun commonSlidesStart() {
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
                type = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PERSON_NAME or
                        InputType.TYPE_TEXT_FLAG_CAP_WORDS)
        )

        addSlide(PhoneFragment.newInstance())
    }

    protected fun commonSlidesEnd() {
        if (!UIUtils.checkReadSmsPermission(this)) {
            addSlide(ReadSmsPermissionFragment())
        }

        addSlide(PresentationFragment.newInstance(
                title = R.string.intro_conclusion_title,
                imageRes = R.drawable.icons8_confetti_512,
                description = R.string.intro_conclusion_description))
    }

    protected fun validateName(binding: ViewIntroTextInputBinding): Pair<Boolean, Int?> {
        var error = false
        var msg: Int? = null

        binding.apply {
            val name = textInput.text.toString().trim()
            if (name.isEmpty()) {
                error = true
                msg = R.string.name_required
            }
            // Save to Preferences
            pref.edit {
                putString(PREF_NAME, name)
            }
        }

        return Pair(error, msg)
    }
}