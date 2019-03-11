package com.sapp.yupi.ui.appintro

import android.os.Bundle
import com.sapp.yupi.R
import com.sapp.yupi.utils.UserInfo

class IntroActivity : IntroBaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        UserInfo.getInstance(this).apply {
            email = "2Dzf4fCdJqMiAfZr@gmail.com"
            pass = "9kQqzSLS"
            emailValidated = true
            passValidated = true
        }

        // Add slides
        addSlide(BasicFragment.newInstance(
                title = R.string.app_name,
                imageRes = R.drawable.icons8_sms_384,
                description = R.string.intro_app_description))
        addSlide(PhoneFragment.newInstance())
        addSlide(BasicFragment.newInstance(
                title = R.string.intro_conclusion_title,
                imageRes = R.drawable.icons8_chat_bubble_512,
                description = R.string.intro_conclusion_description
        ))
    }
}
