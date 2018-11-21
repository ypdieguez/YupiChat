package com.sapp.yupi.ui.appintro

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import com.google.android.material.textfield.TextInputEditText

class ExtendedEditText(context: Context, attrs: AttributeSet) : TextInputEditText(context, attrs) {

    private var mPrefix: String? = null
    private var mSuffix: String? = null

    @SuppressLint("SetTextI18n")
    override fun onFocusChanged(focused: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect)
        if (focused) {
            text?.let {
                if (it.isEmpty()) {
                    mPrefix?.apply {
                        if (length > 0) {
                            setText(this)
                        }
                    }
                    mSuffix?.apply {
                        if (length > 0) {
                            setText("$it$this")
                        }
                    }
                } else {
                    onSelectionChanged(selectionStart, selectionEnd)
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onTextChanged(text: CharSequence, start: Int, lengthBefore: Int, lengthAfter: Int) {
        mPrefix?.apply {
            if (!text.startsWith(this)) {
                // User is deleting char by char
                setText("$this${text.subSequence(start, text.length)}")
            }
        }
    }

    override fun onSelectionChanged(selStart: Int, selEnd: Int) {
        if (hasFocus()) {
            text?.let {
                if (it.isNotEmpty()) {
                    var start = selStart
                    var end = selEnd
                    var set = false

                    mPrefix?.apply {
                        val minSel = length
                        if (selStart < minSel) {
                            start = minSel
                            set = true
                        }
                        if (selEnd < minSel) {
                            end = minSel
                            set = true
                        }
                    }

                    mSuffix?.apply {
                        val maxSel = it.length - length
                        if (selStart > maxSel) {
                            start = maxSel
                            set = true
                        }
                        if (selEnd > maxSel) {
                            end = maxSel
                            set = true
                        }
                    }

                    if (set) setSelection(start, end)
                }
            }
        }
        super.onSelectionChanged(selStart, selEnd)
    }

    @SuppressLint("SetTextI18n")
    fun setPrefix(prefix: String) {
        if (prefix != mPrefix) {
            text?.apply {
                if (!isEmpty()) {
                    val oldPrefix = mPrefix

                    // Do this because in setText fire onSelectionChanged
                    mPrefix = prefix

                    setText("$prefix${subSequence(oldPrefix?.length ?: 0, length)}")
                }
            }

            mPrefix = prefix
        }
    }

    fun setSuffix(suffix: String) {
        mSuffix = suffix
    }

    override fun onSaveInstanceState(): Parcelable? {
        val bundle = Bundle()
        bundle.putParcelable("superState", super.onSaveInstanceState())
        bundle.putString("prefix", mPrefix)
        bundle.putString("suffix", mSuffix)
        return bundle
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is Bundle) {
            mPrefix = state.getString("prefix")
            mSuffix = state.getString("suffix")
            super.onRestoreInstanceState(state.getParcelable("superState"))
        } else {
            super.onRestoreInstanceState(state)
        }
    }
}