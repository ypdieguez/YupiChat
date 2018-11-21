package com.sapp.yupi.ui.appintro

import android.os.Bundle
import android.view.View
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.sapp.yupi.R
import com.sapp.yupi.databinding.ViewIntroPresentationBinding

private const val TAG_FRAGMENT_PRESENTATION = "fragment_presentation"

class PresentationFragment : IntroFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (mBinding as ViewIntroPresentationBinding).apply {
            title.setText(mTitle)
            image.setImageResource(mImageRes)
            description.setText(mDescription)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(@StringRes title: Int, @DrawableRes imageRes: Int,
                        @StringRes description: Int) =
                PresentationFragment().apply {
                    arguments = getBundle(
                            R.layout.view_intro_presentation,
                            TAG_FRAGMENT_PRESENTATION,
                            title,
                            imageRes,
                            description
                    )
                }
    }
}
