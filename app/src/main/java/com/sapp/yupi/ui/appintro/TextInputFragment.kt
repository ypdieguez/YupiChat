package com.sapp.yupi.ui.appintro

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.View
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.sapp.yupi.R
import com.sapp.yupi.databinding.ViewIntroTextInputBinding


private const val ARG_HINT = "input_hint"
private const val ARG_TYPE = "input_type"
private const val ARG_PREFIX = "prefix"
private const val ARG_SUFFIX = "suffix"

open class TextInputFragment : IntroFragment() {

    private var mHint: Int = -1
    private var mType: Int = -1

    // Add default text into text input
    private var mPrefix: String? = null
    private var mSuffix: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mHint = it.getInt(ARG_HINT)
            mType = it.getInt(ARG_TYPE)
            mPrefix = it.getString(ARG_PREFIX)
            mSuffix = it.getString(ARG_SUFFIX)

        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (mBinding as ViewIntroTextInputBinding).apply {
            title.setText(mTitle)
            image.setImageResource(mImageRes)
            description.setText(mDescription)

            textInputLayout.hint = getString(mHint)
            if (mType != InputType.TYPE_TEXT_VARIATION_PASSWORD) textInput.inputType = mType

            mPrefix?.let { textInput.setPrefix(it) }
            mSuffix?.let { textInput.setSuffix(it) }

        }
    }

    override fun onStart() {
        super.onStart()
        (mBinding as ViewIntroTextInputBinding).apply {
            textInput.apply {
                addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {
                        //Do nothing
                    }

                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                        //Do nothing
                    }

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        textInputLayout.apply {
                            if (error != null) {
                                error = null
                            }
                        }
                    }
                })
            }
        }
    }

    @SuppressLint("SetTextI18n")
    fun setPrefix(prefix: String) {
        mPrefix = prefix
        mBinding?.let {
            (it as ViewIntroTextInputBinding).textInput.setPrefix(prefix)
        }

    }

    @SuppressLint("SetTextI18n")
    fun setSuffix(suffix: String) {
        mSuffix = suffix
        mBinding?.let {
            (it as ViewIntroTextInputBinding).textInput.setSuffix(suffix)
        }

    }

    override fun onUserIllegallyRequestedNextPage() {
        (mBinding as ViewIntroTextInputBinding).apply {
            textInputLayout.error = mError.second
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(fragmentTag: String, @StringRes title: Int, @DrawableRes imageRes: Int,
                        @StringRes description: Int, @StringRes hint: Int, type: Int,
                        prefix: String? = null, suffix: String? = null) =
                TextInputFragment().apply {
                    arguments = getBundle(
                            R.layout.view_intro_text_input,
                            fragmentTag,
                            title, imageRes, description
                    ).apply {
                        putInt(ARG_HINT, hint)
                        putInt(ARG_TYPE, type)

                        prefix?.let { putString(ARG_PREFIX, it) }
                        suffix?.let { putString(ARG_SUFFIX, it) }
                    }
                }
    }
}