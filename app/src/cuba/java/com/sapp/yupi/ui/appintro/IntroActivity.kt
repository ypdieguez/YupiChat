package com.sapp.yupi.ui.appintro

import android.os.Bundle
import com.sapp.yupi.R
import com.sapp.yupi.util.UserPrefUtil

class IntroActivity : IntroBaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        UserPrefUtil.setEmail("2Dzf4fCdJqMiAfZr@gmail.com")
        UserPrefUtil.setEmailPass("9kQqzSLS")

        // Add slides
        addSlide(BasicFragment.newInstance(
                title = R.string.app_name,
                imageRes = R.drawable.icons8_sms_384,
                description = R.string.intro_app_description))
        addSlide(PhoneFragment.newInstance())
        addSlide(BasicFragment.newInstance(
                title = R.string.intro_conclusion_title,
                imageRes = R.drawable.icons8_confetti_512,
                description = R.string.intro_conclusion_description
        ))
    }
}
