package com.sapp.yupi.ui.appintro

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.text.Editable
import android.text.Selection
import android.text.TextWatcher
import android.util.AttributeSet
import com.google.android.material.textfield.TextInputEditText

class ExtendedEditText(context: Context, attrs: AttributeSet) : TextInputEditText(context, attrs) {

    var prefix: String? = null
        @SuppressLint("SetTextI18n")
        set(value) {
            if (value != field) {
                text?.apply {
                    if (!isEmpty()) {
                        val oldPrefix = field

                        // Do this because in setText fire onSelectionChanged
                        field = value

                        setText("$value${subSequence(oldPrefix?.length ?: 0, length)}")
                    } else {
                        append(value)
                    }
                }

                field = value
            }
        }

    var suffix: String? = null

    private var mWatcher: TextWatcher = object : TextWatcher {
        /**
         * Indicates the change was caused by ourselves.
         */
        private var mSelfChange = false

        override fun afterTextChanged(s: Editable?) {
            if (mSelfChange) {
                // Ignore the change caused by s.replace().
                return
            }

            prefix?.apply {
                if (s != null) {
                    val first = s.substring(0, Selection.getSelectionEnd(s))
                    if (first < this) {
                        mSelfChange = true
                        val second = s.substring(Selection.getSelectionEnd(s))
                        val text = "$this$second"
                        s.replace(0, s.length, text, 0, text.length)
                        // The text could be changed by other TextWatcher after we changed it. If we found the
                        // text is not the one we were expecting, just give up calling setSelection().
                        if (text == s.toString()) {
                            Selection.setSelection(s, length)
                        }
                        mSelfChange = false
                    }
                }
            }
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            // Do nothing
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            // Do nothing
        }
    }

    init {
        addTextChangedListener(mWatcher)
    }

    @SuppressLint("SetTextI18n")
    override fun onFocusChanged(focused: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect)
        if (focused) {
            text?.also {
                if (it.isEmpty()) {
                    prefix?.apply {
                        if (length > 0) {
                            setText(this)
                        }
                    }
                    suffix?.apply {
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

    override fun onSelectionChanged(selStart: Int, selEnd: Int) {
        text?.let {
            if (it.isNotEmpty()) {
                var start = selStart
                var end = selEnd
                var set = false

                prefix?.apply {
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

                suffix?.apply {
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
        super.onSelectionChanged(selStart, selEnd)
    }

    //    override fun onSaveInstanceState(): Parcelable? {
//        val bundle = Bundle()
//        bundle.putParcelable("superState", super.onSaveInstanceState())
//        bundle.putString("prefix", mPrefix)
//        bundle.putString("suffix", mSuffix)
//        return bundle
//    }
//
//    override fun onRestoreInstanceState(state: Parcelable?) {
//        if (state is Bundle) {
//            mPrefix = state.getString("prefix")
//            mSuffix = state.getString("suffix")
//            super.onRestoreInstanceState(state.getParcelable("superState"))
//        } else {
//            super.onRestoreInstanceState(state)
//        }
//    }

//    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
//        if (keyCode == KeyEvent.KEYCODE_ENTER) {
//            hideKeyboard()
//        }
//        return super.onKeyDown(keyCode, event)
//    }
//
//    private fun hideKeyboard() {
//            val inputMethodManager =
//                    context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//            inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
//    }
}