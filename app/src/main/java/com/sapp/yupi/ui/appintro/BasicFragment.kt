package com.sapp.yupi.ui.appintro

import android.os.Bundle
import android.view.View
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.sapp.yupi.R
import com.sapp.yupi.databinding.ViewIntroBinding

private const val TAG_FRAGMENT_BASIC = "fragment_presentation"

class BasicFragment : IntroFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (mBinding as ViewIntroBinding).apply {
            title.setText(mTitle)
            image.setImageResource(mImageRes)
            description.setText(mDescription)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(@StringRes title: Int, @DrawableRes imageRes: Int,
                        @StringRes description: Int) =
                BasicFragment().apply {
                    arguments = getBundle(
                            R.layout.view_intro,
                            TAG_FRAGMENT_BASIC,
                            title,
                            imageRes,
                            description
                    )
                }
    }
}
