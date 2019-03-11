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
import com.sapp.yupi.utils.*

const val TAG_FRAGMENT_EMAIL = "fragment_mail"

class EmailFragment : IntroFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (mBinding as ViewIntroEmailBinding).apply {
            textInputEmail.apply {
                UserInfo.getInstance(context).email.apply {
                    if (isNotEmpty()) {
                        setText(this)
                    }
                }

                suffix = "@nauta.cu"

                setOnTouchListener { _, _ ->
                    extraFields.textViewError.visibility = View.GONE
                    false
                }
            }

            textInputPass.apply {
                UserInfo.getInstance(context).pass.apply {
                    if (isNotEmpty()) {
                        setText(this)
                    }
                }

                setOnTouchListener { _, _ ->
                    extraFields.textViewError.visibility = View.GONE
                    false
                }

                setOnFocusChangeListener { _, hasFocus ->
                    val color: Int = ContextCompat.getColor(context, if (hasFocus)
                        R.color.primary_color else R.color.secondary_text_color)
                    textInputLayoutPass.setPasswordVisibilityToggleTintList(
                            ColorStateList.valueOf(color))
                }
            }
        }

        UserInfo.getInstance(context!!).apply {
            isValidated = emailValidated && passValidated
        }
    }

    override fun isPolicyRespected(): Boolean {
        if (!isValidating) {
            showError(false)
            if (isValidEmail() && isValidPass() && !isValidated && isNetworkConnected()) {
                ValidateEmailAsyncTask().execute()
                isValidating = true
            }
        }

        return isValidated
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

    private fun isValidEmail(): Boolean {
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
                UserInfo.getInstance(context!!).apply {
                    if (this.email != email) {
                        // Save to Preferences
                        this.email = email
                        this.emailValidated = false

                        isValidated = false
                    }
                }
                true
            }
        }
    }

    private fun isValidPass(): Boolean {
        (mBinding as ViewIntroEmailBinding).apply {
            val pass = textInputPass.text.toString()
            errorMsgId = when {
                pass.isEmpty() -> R.string.pass_required
                else -> -1
            }

            return if (errorMsgId != -1) {
                false
            } else {
                UserInfo.getInstance(context!!).apply {
                    if (this.pass != pass) {
                        // Save to Preferences
                        this.pass = pass
                        this.passValidated = false

                        isValidated = false
                    }
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
            val email = UserInfo.getInstance(context!!).email
            return Email.getInstance(context!!).send(BuildConfig.SUBSCRIBER_EMAIL, email, email)
        }

        override fun onPostExecute(result: Byte) {
            (mBinding as ViewIntroEmailBinding).apply {

                setViewStateInActivationMode(true)

                errorMsgId = when (result) {
                    STATUS_MAIL_CONNECT_EXCEPTION -> R.string.host_not_connected
                    STATUS_AUTHENTICATION_FAILED_EXCEPTION -> R.string.wrong_user_or_password
                    STATUS_OTHER_EXCEPTION -> R.string.unknown_error
                    else -> -1
                }

                isValidated = if (errorMsgId != -1) {
                    showError(true)
                    false
                } else {
                    showError(false)
                    UserInfo.getInstance(context!!).apply {
                        emailValidated = true
                        passValidated = true
                    }
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