package com.sapp.yupi.ui.appintro

import android.os.Bundle
import androidx.databinding.ViewDataBinding
import com.sapp.yupi.R
import com.sapp.yupi.databinding.ViewIntroTextInputBinding
import com.sapp.yupi.util.UserPrefUtil

class IntroActivity : IntroBaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        UserPrefUtil.setEmail("2Dzf4fCdJqMiAfZr@gmail.com")
        UserPrefUtil.setPass("Ass")

        // Add slides
        addSlide(PresentationFragment.newInstance(
                title = R.string.app_name,
                imageRes = R.drawable.icons8_sms_384,
                description = R.string.intro_app_description))
        addSlide(PhoneFragment.newInstance())
    }

    override fun validate(binding: ViewDataBinding, tag: String): Pair<Boolean, Int?> {
        return when (tag) {
            TAG_FRAGMENT_NAME -> {
                validateName(binding as ViewIntroTextInputBinding)
            }
//            TAG_FRAGMENT_PHONE -> {
//                validatePhone(binding as ViewIntroPhoneBinding)
//            }
            else -> Pair(false, null)
        }
    }
}
