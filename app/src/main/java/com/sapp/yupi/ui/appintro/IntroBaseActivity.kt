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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Enable Wizard mode
        wizardMode = true

        // Define colors
        val colorPrimary = ContextCompat.getColor(this, R.color.primary_color)
        val colorBackground = ContextCompat.getColor(this, R.color.background)

        // Set properties
        showSkipButton(true)
        setSkipText(getString(R.string.back))
        setColorSkipButton(colorPrimary)
        setIndicatorColor(colorPrimary, colorPrimary)
        setNextArrowColor(colorPrimary)
        setColorDoneText(colorPrimary)
        setSeparatorColor(colorPrimary)
        (backButton as ImageButton).apply {
            setColorFilter(colorPrimary)
//            background = ContextCompat.getDrawable(this@IntroBaseActivity, R.drawable.back)
        }
        setBarColor(colorBackground)
        setScrollDurationFactor(2)
        setSwipeLock(true)
    }

    override fun onDonePressed(currentFragment: Fragment?) {
        // Phone number is validated in IncomingMsgWorker worker class

        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    override fun onSlideChanged(oldFragment: Fragment?, newFragment: Fragment?) {
        newFragment?.apply {
            if(this == fragments.first()) {
                showSkipButton(true)
            } else {
                showSkipButton(false)
            }
        }
    }

    override fun onSkipPressed() {
        startActivity(Intent(this, IntroActivity::class.java))
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