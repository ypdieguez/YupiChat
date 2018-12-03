package com.sapp.yupi.ui.appintro

import android.content.Context
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

private const val ARG_LAYOUT_RES = "layout_res"
private const val ARG_FRAGMENT_TAG = "fragment_tag"
private const val ARG_TITLE = "title"
private const val ARG_IMAGE_RES = "image_res"
private const val ARG_DESCRIPTION = "description"

open class IntroFragment : Fragment(), ISlidePolicy {

    interface PolicyListener {
        /**
         * Validate the view
         *
         * @return Pair<Boolean, String?> First parameter: Return true if view has error or false if
         *                                not.
         *                                Second parameter: The message of error or null.
         */
        fun validate(binding: ViewDataBinding, tag: String): Pair<Boolean, String?>
    }

    protected var mTitle: Int = -1
    protected var mImageRes: Int = -1
    protected var mDescription: Int = -1

    private var mLayoutRes: Int = -1
    private var mTag: String = "fragment_intro"

    protected var mBinding: ViewDataBinding? = null

    private var mListener: PolicyListener? = null
    protected lateinit var mError: Pair<Boolean, String?>

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

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is PolicyListener) {
            mListener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
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

    override fun isPolicyRespected(): Boolean {
        mListener?.apply {
            mBinding?.let {
                mError = validate(it, mTag)
                return !mError.first
            }
        }

        return true
    }

    override fun onUserIllegallyRequestedNextPage() {
        // Do nothing
    }
}
