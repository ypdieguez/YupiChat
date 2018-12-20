package com.sapp.yupi.ui.appintro

import android.os.Bundle
import android.util.Patterns
import android.view.View
import com.sapp.yupi.Mail
import com.sapp.yupi.R
import com.sapp.yupi.STATUS_SUCCESS
import com.sapp.yupi.databinding.ViewIntroEmailBinding
import com.sapp.yupi.util.NetworkUtil
import com.sapp.yupi.util.UserPrefUtil

class EmailFragment : IntroFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (mBinding as ViewIntroEmailBinding).apply {
            textInputEmail.setSuffix("@nauta.cu")
        }
    }

    override fun isPolicyRespected(): Boolean {
        var valid = validateEmail() && validatePass()
        if (valid) {
            valid = validateConnected()
            if (valid) {
                val status = Mail.send(context!!, UserPrefUtil.getEmail(),
                        "YuupiWorld", "Comprobando disponiblidad del correo")
                if (status != STATUS_SUCCESS) {
                    valid = false
                }
            }
        }
        return valid
    }


    private fun validateEmail(): Boolean {
        var error = false

        (mBinding as ViewIntroEmailBinding).apply {
            val email = textInputEmail.text.toString().trim()
            if (email.isEmpty()) {
                error = true
                textInputLayoutEmail.error  = getString(R.string.email_required)
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                error = true
                textInputLayoutEmail.error  = getString(R.string.email_not_valid)

            } else if (!email.endsWith("@nauta.cu")) {
                error = true
                textInputLayoutEmail.error  = getString(R.string.email_is_not_nauta)
            }
            // Save to Preferences
            UserPrefUtil.setEmail(email)
        }

        return !error
    }

    private fun validatePass(): Boolean {
        var error = false

        (mBinding as ViewIntroEmailBinding).apply {
            val pass = textInputPass.text.toString()
            if (pass.isEmpty()) {
                error = true
                textInputLayoutPass.error = getString(R.string.pass_required)
            }
            // Save to Preferences
            UserPrefUtil.setEmailPass(pass)
        }

        return !error
    }

    private fun validateConnected(): Boolean {
        if(!NetworkUtil.isConnected()) {
            (mBinding as ViewIntroEmailBinding).apply {
                error.text = "Error de conexión"
            }
            return false
        }
        return true
    }

    companion object {
        @JvmStatic
        fun newInstance() =
                PhoneFragment().apply {
                    arguments = getBundle(
                            layoutRes = R.layout.view_intro_email,
                            fragmentTag = TAG_FRAGMENT_EMAIL
                    )
                }
    }
}