package com.sapp.yupi.ui.appintro

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.os.AsyncTask
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.ProgressBar
import androidx.core.content.ContextCompat
import com.sapp.yupi.*
import com.sapp.yupi.databinding.ViewIntroEmailBinding
import com.sapp.yupi.util.UserPrefUtil

const val TAG_FRAGMENT_EMAIL = "fragment_mail"

class EmailFragment : IntroFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (mBinding as ViewIntroEmailBinding).apply {
            textInputEmail.apply {
                setSuffix("@nauta.cu")
                setOnTouchListener { _, _ ->
                    extraFields.textViewError.visibility = View.GONE
                    false
                }
            }

            textInputPass.apply {
                setOnTouchListener { _, _ ->
                    extraFields.textViewError.visibility = View.GONE
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
            showError(false)
            if (validateEmail() && validatePass() && !isValid && validateNetworkConnected()) {
                ValidateEmailAsyncTask().execute()
                isValidating = true
            }
        }

        return isValid
    }

    override fun setViewStateInActivationMode(enable: Boolean) {
        super.setViewStateInActivationMode(enable)

        (mBinding as ViewIntroEmailBinding).apply {
            extraFields.spinKit.visibility = if (enable) ProgressBar.GONE else ProgressBar.VISIBLE
            textInputEmail.isEnabled = enable
            textInputLayoutEmail.isEnabled = enable
            textInputPass.isEnabled = enable
            textInputLayoutPass.isEnabled = enable
            textInputLayoutPass.isPasswordVisibilityToggleEnabled = enable
        }
    }

    override fun showError(show: Boolean) {
        (mBinding as ViewIntroEmailBinding).extraFields.apply {
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
                if (UserPrefUtil.getEmailPass() != pass) {
                    // Save to Preferences
                    UserPrefUtil.setEmailPass(pass)
                    isValid = false
                }

                true
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private inner class ValidateEmailAsyncTask : AsyncTask<String, Void, Byte>() {
        override fun onPreExecute() {
            setViewStateInActivationMode(false)
        }

        override fun doInBackground(vararg strings: String): Byte {
            return Email.send(UserPrefUtil.getEmail(), getString(R.string.app_name),
                    getString(R.string.validate_email_account))
        }

        override fun onPostExecute(result: Byte) {
            (mBinding as ViewIntroEmailBinding).apply {

                setViewStateInActivationMode(true)

                errorMsgId = when (result) {
                    STATUS_MAIL_CONNECT_EXCEPTION -> R.string.host_not_connected
                    STATUS_AUTHENTICATION_FAILED_EXCEPTION -> R.string.wrong_user_or_password
                    STATUS_OHTER_EXCEPTION -> R.string.unknow_error
                    else -> -1
                }

                isValid = if (errorMsgId != -1) {
                    showError(true)
                    false
                } else {
                    showError(false)
                    goToNextSlide()
                    true
                }

                isValidating = false
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