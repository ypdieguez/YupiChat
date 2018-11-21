package com.sapp.yupi.ui.appintro

import com.sapp.yupi.R

class CountryFragment : IntroFragment() {

    companion object {
        @JvmStatic
        fun newInstance() = CountryFragment().apply {
            arguments = getBundle(
                    layoutRes = R.layout.view_intro_country,
                    fragmentTag = TAG_FRAGMENT_COUNTRY,
                    title = R.string.intro_country_title,
                    imageRes = R.drawable.icons8_country_500,
                    description = R.string.intro_country_description
            )
        }
    }
}
