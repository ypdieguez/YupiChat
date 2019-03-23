package com.sapp.yupi.ui.appintro

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.github.paolorotolo.appintro.AppIntro
import com.sapp.yupi.R
import com.sapp.yupi.ui.MainActivity

abstract class IntroBaseActivity : AppIntro() {

    protected var done = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Enable Wizard mode
        wizardMode = true

        // Define colors
        val colorPrimary = ContextCompat.getColor(this, R.color.primary_color)
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
        // Phone number is validated in IncomingMsgWorker worker class

        done = true

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