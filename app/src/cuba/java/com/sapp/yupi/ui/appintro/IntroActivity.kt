package com.sapp.yupi.ui.appintro

import android.os.Bundle
import android.util.Patterns
import androidx.core.content.edit
import androidx.databinding.ViewDataBinding
import com.google.i18n.phonenumbers.NumberParseException
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.sapp.yupi.R
import com.sapp.yupi.databinding.ViewIntroPhoneBinding
import com.sapp.yupi.databinding.ViewIntroTextInputBinding

class IntroActivity : IntroBaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Add slides
        commonSlidesStart()
        addSlide(PhoneFragment.newInstance())
        commonSlidesEnd()
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
