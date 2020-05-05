package com.sapp.yupi.ui.appintro.cuba

import android.os.Bundle
import com.sapp.yupi.BuildConfig
import com.sapp.yupi.R
import com.sapp.yupi.ui.appintro.DescriptionFragment
import com.sapp.yupi.ui.appintro.IntroBaseActivity
import com.sapp.yupi.UserPref

class IntroCubaActivity : IntroBaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        UserPref.getInstance(this).apply {
            email = "ypdieguez@nauta.cu"
            emailValidated = true
            pass = "alexa2019"
            passValidated = true
            phone = "+5352871805"
            phoneValidated = true


            from = UserPref.IN_CUBA

            host = BuildConfig.CUBA_HOST
            port = BuildConfig.CUBA_PORT
            sslEnabled = BuildConfig.CUBA_SSL_ENABLED
        }

        // Add slides
        addSlide(DescriptionFragment.newInstance(
                title = R.string.app_name,
                imageRes = R.drawable.icons8_sms_384,
                description = R.string.intro_app_description_cuba))
        addSlide(EmailFragment.newInstance())
        addSlide(PhoneFragment.newInstance())
        addSlide(DescriptionFragment.newInstance(
                title = R.string.intro_conclusion_title,
                imageRes = R.drawable.icons8_chat_bubble_512,
                description = R.string.intro_conclusion_description
        ))
    }
}
