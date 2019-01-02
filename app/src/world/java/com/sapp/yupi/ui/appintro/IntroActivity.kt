package com.sapp.yupi.ui.appintro

import android.os.Bundle
import com.sapp.yupi.R

class IntroActivity : IntroBaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Add slides
        addSlide(PresentationFragment.newInstance(
                title = R.string.app_name,
                imageRes = R.drawable.icons8_sms_384,
                description = R.string.intro_app_description))
        addSlide(EmailFragment.newInstance())
        addSlide(PhoneFragment.newInstance())
        addSlide(PresentationFragment.newInstance(
                title = R.string.intro_conclusion_title,
                imageRes = R.drawable.icons8_confetti_512,
                description = R.string.intro_conclusion_description
        ))
    }
}
