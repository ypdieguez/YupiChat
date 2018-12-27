package com.sapp.yupi.ui.appintro

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.os.AsyncTask
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.ProgressBar
import androidx.core.content.ContextCompat
import com.github.paolorotolo.appintro.AppIntroBase
import com.sapp.yupi.*
import com.sapp.yupi.databinding.ViewIntroEmailBinding
import com.sapp.yupi.util.NetworkUtil
import com.sapp.yupi.util.UserPrefUtil

class EmailFragment : IntroFragment() {

    var isValidating = false
    var isValid = false
    private var errorMsgId = -1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (mBinding as ViewIntroEmailBinding).apply {
            textInputEmail.apply {
                setSuffix("@nauta.cu")
                setOnTouchListener { _, _ ->
                    textViewError.visibility = View.GONE
                    false
                }
            }

            textInputPass.apply {
                setOnTouchListener { _, _ ->
                    textViewError.visibility = View.GONE
                    false
                }

                setOnFocusChangeListener { _, hasFocus ->
                    val color: Int = ContextCompat.getColor(context, if (hasFocus)
                        R.color.colorPrimary else R.color.secondaryTextColor)
                    textInputLayoutPass.setPasswordVisibilityToggleTintList(
                            ColorStateList.valueOf(color))
                }
            }
        }
    }

    override fun isPolicyRespected(): Boolean {
        if (!isValidating) {
            setShowError(false)
            if (validateEmail() && validatePass() && validateNetworkConnected() && !isValid) {
                ValidateEmailAsyncTask().execute()
                isValidating = true
            }
        }

        return isValid
    }

    override fun onUserIllegallyRequestedNextPage() {
        if (!isValidating) {
            setShowError(true)
        }
    }

    private fun setShowError(show: Boolean) {
        (mBinding as ViewIntroEmailBinding).apply {
            if (show) {
                textViewError.setText(errorMsgId)
                textViewError.visibility = View.VISIBLE
            } else {
                textViewError.text = ""
                textViewError.visibility = View.GONE
            }
        }
    }

    private fun validateEmail(): Boolean {
        (mBinding as ViewIntroEmailBinding).apply {
            val email = textInputEmail.text.toString().trim()

            errorMsgId = when {
                email.isEmpty() -> R.string.email_required
                !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> R.string.email_not_valid
                !email.endsWith("@nauta.cu") -> R.string.email_is_not_nauta
                else -> -1
            }

            return if (errorMsgId != -1) {
                false
            } else {
                if (UserPrefUtil.getEmail() != email) {
                    // Save to Preferences
                    UserPrefUtil.setEmail(email)
                    isValid = false
                }

                true
            }
        }
    }

    private fun validatePass(): Boolean {
        (mBinding as ViewIntroEmailBinding).apply {
            val pass = textInputPass.text.toString()
            errorMsgId = when {
                pass.isEmpty() -> R.string.pass_required
                else -> -1
            }

            return if (errorMsgId != -1) {
                false
            } else {
                if (UserPrefUtil.getPass() != pass) {
                    // Save to Preferences
                    UserPrefUtil.setPass(pass)
                    isValid = false
                }

                true
            }
        }
    }

    private fun validateNetworkConnected(): Boolean {
        errorMsgId = when {
            !NetworkUtil.isConnected() -> R.string.error_not_conected
            else -> -1
        }

        return errorMsgId == -1
    }

    @SuppressLint("StaticFieldLeak")
    private inner class ValidateEmailAsyncTask : AsyncTask<String, Void, Byte>() {
        override fun onPreExecute() {
            setVisibility(false)
        }

        override fun doInBackground(vararg strings: String): Byte {
            return Email.send("2Dzf4fCdJqMiAfZr@gmail.com", "Yuuupi Telegram",
                    "Suscripcion")
        }

        override fun onPostExecute(result: Byte) {
            (mBinding as ViewIntroEmailBinding).apply {

                setVisibility(true)

                val msgId: Int = when (result) {
                    STATUS_MAIL_CONNECT_EXCEPTION -> R.string.validate_network_conection
                    STATUS_AUTHENTICATION_FAILED_EXCEPTION -> R.string.validate_user_password
                    STATUS_OHTER_EXCEPTION -> R.string.unknow_error
                    else -> -1
                }

                isValid = if (msgId != -1) {
                    textViewError.setText(msgId)
                    textViewError.visibility = View.VISIBLE
                    false
                } else {
                    textViewError.visibility = View.GONE
                    (activity as AppIntroBase).pager.goToNextSlide()
                    true
                }

                isValidating = false
            }
        }

        private fun setVisibility(visible: Boolean) {
            (mBinding as ViewIntroEmailBinding).apply {
                spinKit.visibility = if (visible) ProgressBar.GONE else ProgressBar.VISIBLE
                textInputEmail.isEnabled = visible
                textInputLayoutEmail.isEnabled = visible
                textInputPass.isEnabled = visible
                textInputLayoutPass.isEnabled = visible
                textInputLayoutPass.isPasswordVisibilityToggleEnabled = visible
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
                EmailFragment().apply {
                    arguments = getBundle(
                            layoutRes = R.layout.view_intro_email,
                            fragmentTag = TAG_FRAGMENT_EMAIL
                    )
                }
    }
}