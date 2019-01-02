package com.sapp.yupi.ui.appintro

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import com.github.paolorotolo.appintro.AppIntro
import com.sapp.yupi.R
import com.sapp.yupi.ui.FIRST_LAUNCH
import com.sapp.yupi.ui.MainActivity

const val USER_PREFERENCES = "user_preferences"

const val PREF_PHONE = "phone"
const val PREF_EMAIL = "email"
const val PREF_EMAIL_PASS = "email_pass"

const val TAG_FRAGMENT_PHONE = "fragment_phone"
const val TAG_FRAGMENT_EMAIL = "fragment_mail"

abstract class IntroBaseActivity : AppIntro() {

    private lateinit var pref: SharedPreferences

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

    /**
     * Helper method for displaying a view
     *
     * @param show   Whether the view should be visible or not
     */
    fun setButtonState(show: Boolean) {
        val visibility = if (show) {
            View.VISIBLE
        } else {
            View.INVISIBLE
        }

        nextButton.visibility = visibility
        backButton.visibility = visibility
    }
}