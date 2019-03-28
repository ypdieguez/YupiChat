package com.sapp.yupi.ui.appintro

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.github.paolorotolo.appintro.ISlidePolicy
import com.sapp.yupi.R
import com.sapp.yupi.utils.NetworkStatus

private const val ARG_LAYOUT_RES = "layout_res"
private const val ARG_FRAGMENT_TAG = "fragment_tag"
private const val ARG_TITLE = "title"
private const val ARG_IMAGE_RES = "image_res"
private const val ARG_DESCRIPTION = "description"

abstract class IntroFragment : Fragment(), ISlidePolicy {

    protected var mTitle: Int = -1
    protected var mImageRes: Int = -1
    protected var mDescription: Int = -1

    private var mLayoutRes: Int = -1
    private var mTag: String = "fragment_intro"

    protected var mBinding: ViewDataBinding? = null

    protected var errorMsgId = -1

    var isValidating = false
    var isValidated = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mTitle = it.getInt(ARG_TITLE)
            mImageRes = it.getInt(ARG_IMAGE_RES)
            mDescription = it.getInt(ARG_DESCRIPTION)

            mLayoutRes = it.getInt(ARG_LAYOUT_RES)
            mTag = it.getString(ARG_FRAGMENT_TAG)!!
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, mLayoutRes, container, false)
        return mBinding!!.root
    }

    override fun isPolicyRespected() = true

    override fun onUserIllegallyRequestedNextPage() {
        if (!isValidating) {
            showError(true)
        }
    }

    protected open fun showError(show: Boolean) {
        // Do nothing
    }

    protected open fun setViewStateInActivationMode(enable: Boolean) {
        (activity as IntroBaseActivity).setButtonState(enable)
    }

    protected fun goToNextSlide() {
        (activity as IntroBaseActivity).pager.goToNextSlide()
    }

    protected fun isNetworkConnected(): Boolean {
        errorMsgId = when {
            !NetworkStatus.isConnected() -> {
                isValidated = false
                R.string.network_not_connected
            }
            else -> -1
        }

        return errorMsgId == -1
    }

    fun getBundle(@LayoutRes layoutRes: Int, fragmentTag: String, @StringRes title: Int = -1,
                  @DrawableRes imageRes: Int = -1, @StringRes description: Int = -1) =
            Bundle().apply {
                putInt(ARG_LAYOUT_RES, layoutRes)
                putString(ARG_FRAGMENT_TAG, fragmentTag)

                putInt(ARG_TITLE, title)
                putInt(ARG_IMAGE_RES, imageRes)
                putInt(ARG_DESCRIPTION, description)
            }

}
