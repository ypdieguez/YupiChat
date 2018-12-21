package com.sapp.yupi.ui.appintro

import android.os.Bundle
import android.text.InputType
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
        addSlide(EmailFragment.newInstance())
        addSlide(PhoneFragment.newInstance())
//        commonSlidesStart()
//        addSlide(TextInputFragment.newInstance(
//                fragmentTag = TAG_FRAGMENT_EMAIL,
//                title = R.string.intro_email_title,
//                imageRes = R.drawable.icons8_new_post_512,
//                description = R.string.intro_email_description,
//                hint = R.string.intro_email_title,
//                type = InputType.TYPE_CLASS_TEXT,
//                suffix = "@nauta.cu"))
//        addSlide(TextInputFragment.newInstance(
//                fragmentTag = TAG_FRAGMENT_MAIL_PASS,
//                title = R.string.intro_pass_title,
//                imageRes = R.drawable.icons8_password_480,
//                description = R.string.intro_pass_description,
//                hint = R.string.intro_pass_title,
//                type = InputType.TYPE_TEXT_VARIATION_PASSWORD))
//
//        commonSlidesEnd()
    }

    override fun validate(binding: ViewDataBinding, tag: String): Pair<Boolean, Int?> {
        return when (tag) {
            TAG_FRAGMENT_NAME -> {
                validateName(binding as ViewIntroTextInputBinding)
            }
            else -> Pair(false, null)
        }
    }
}
