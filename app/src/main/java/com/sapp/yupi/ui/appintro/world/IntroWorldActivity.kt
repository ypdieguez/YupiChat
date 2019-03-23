package com.sapp.yupi.ui.appintro.world

import android.os.Bundle
import com.sapp.yupi.BuildConfig
import com.sapp.yupi.R
import com.sapp.yupi.ui.appintro.DescriptionFragment
import com.sapp.yupi.ui.appintro.IntroBaseActivity
import com.sapp.yupi.Config

class IntroWorldActivity : IntroBaseActivity() {

    // Save config for Cuba
    private var cubaEmail = ""
    private var cubaEmailPass = ""
    private var cubaEmailValidated = false
    private var cubaEmailPassValidated = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Config.getInstance(this).apply {
            // Save Cuba email
            cubaEmail = email
            cubaEmailPass = pass
            cubaEmailValidated = emailValidated
            cubaEmailPassValidated = passValidated

            email = BuildConfig.WORLD_SENDER_EMAIL
            pass = BuildConfig.WORLD_SENDER_EMAIL_PASS
            emailValidated = true
            passValidated = true

            host = BuildConfig.WORLD_HOST
            port = BuildConfig.WORLD_PORT
            sslEnabled = BuildConfig.WORLD_SSL_ENABLED
        }

        // Add slides
        addSlide(DescriptionFragment.newInstance(
                title = R.string.app_name,
                imageRes = R.drawable.icons8_sms_384,
                description = R.string.intro_app_description_world))
        addSlide(PhoneFragment.newInstance())
        addSlide(DescriptionFragment.newInstance(
                title = R.string.intro_conclusion_title,
                imageRes = R.drawable.icons8_chat_bubble_512,
                description = R.string.intro_conclusion_description
        ))
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!done) {
            // Restore cuba email
            Config.getInstance(this).apply {
                email = cubaEmail
                pass = cubaEmailPass
                emailValidated = cubaEmailValidated
                passValidated = cubaEmailPassValidated
            }
        }
    }
}
