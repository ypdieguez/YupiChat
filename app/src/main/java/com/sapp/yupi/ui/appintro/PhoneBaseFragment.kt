package com.sapp.yupi.ui.appintro

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.AsyncTask
import android.provider.Settings
import android.provider.Telephony
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import com.sapp.yupi.*
import com.sapp.yupi.databinding.ViewIntroPhoneBinding
import com.sapp.yupi.observers.ActivationListener
import com.sapp.yupi.observers.ActivationObserver

abstract class PhoneBaseFragment : IntroFragment() {
    var isFirstTime = true
    var isValidating = false
    var isValid = false
    var errorMsgId = -1

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray) {
        checkPermissions()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        checkPermissions()
    }

    override fun isPolicyRespected(): Boolean {
        if (!isValidating) {
            setShowError(false)
            if (validatePhone() && !isValid) {
                ValidatePhoneAsyncTask().execute()
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
        (mBinding as ViewIntroPhoneBinding).apply {
            if (show) {
                textViewError.setText(errorMsgId)
                textViewError.visibility = View.VISIBLE
            } else {
                textViewError.text = ""
                textViewError.visibility = View.GONE
            }
        }
    }

    protected fun checkPermissions() {
        context?.let {
            if (ContextCompat.checkSelfPermission(it, Manifest.permission.READ_PHONE_STATE) ==
                    PackageManager.PERMISSION_GRANTED) {
                tryGetPhoneNumber()
            }

            if (ContextCompat.checkSelfPermission(it, Manifest.permission.READ_SMS) !=
                    PackageManager.PERMISSION_GRANTED) {
                askForPermissions()
            }
        }
    }

    private fun askForPermissions() {
        context?.let { context ->
            val readPhoneState =
                    ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) ==
                            PackageManager.PERMISSION_GRANTED
            val readSms =
                    ContextCompat.checkSelfPermission(context, Manifest.permission.READ_SMS) ==
                            PackageManager.PERMISSION_GRANTED

            if (!readPhoneState || !readSms) {

                val readPhoneStatePermanentlyDenied: Boolean
                val readSmsPermanentlyDenied: Boolean
                context.getSharedPreferences(PERMISSION_PREFERENCES, Context.MODE_PRIVATE).apply {
                    readPhoneStatePermanentlyDenied =
                            !getBoolean(PREF_FIRST_READ_PHONE_STATE_PERMISSION, true) &&
                            !shouldShowRequestPermissionRationale(Manifest.permission.READ_PHONE_STATE)

                    readSmsPermanentlyDenied =
                            !getBoolean(PREF_FIRST_READ_SMS_PERMISSION, true) &&
                            !shouldShowRequestPermissionRationale(Manifest.permission.READ_SMS)

                    edit {
                        putBoolean(PREF_FIRST_READ_PHONE_STATE_PERMISSION, false)
                        putBoolean(PREF_FIRST_READ_SMS_PERMISSION, false)
                    }
                }

                val dialog = Dialog(context)
                dialog.apply {
                    setContentView(R.layout.permission_dialog)
                    window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    setCanceledOnTouchOutside(false)
                    setCancelable(false)

                    val size = resources.getDimensionPixelSize(R.dimen.icon)
                    val iconsContainer = findViewById<LinearLayout>(R.id.icons_container)

                    val text: Int
                    var appendText = ""
                    if (!readPhoneState && !readSms) {

                        val plusSize = resources.getDimensionPixelSize(R.dimen.plus)

                        // ImageView Setup
                        val iconPhone = ImageView(context)
                        val iconPlus = ImageView(context)
                        val iconSms = ImageView(context)
                        // Setting image resource
                        iconPhone.setImageResource(R.drawable.phone)
                        iconPlus.setImageResource(R.drawable.plus)
                        iconSms.setImageResource(R.drawable.message_processing)
                        // Setting image size
                        iconPhone.layoutParams = LinearLayout.LayoutParams(size, size)
                        iconPlus.layoutParams = LinearLayout.LayoutParams(plusSize, plusSize)
                        iconSms.layoutParams = LinearLayout.LayoutParams(size, size)
                        // Adding view to layout
                        iconsContainer.addView(iconPhone)
                        iconsContainer.addView(iconPlus)
                        iconsContainer.addView(iconSms)

                        // Add text
                        text = R.string.perm_read_phone_state_and_read_sms
                        if (readPhoneStatePermanentlyDenied || readSmsPermanentlyDenied) {
                            appendText = getString(
                                    R.string.perm_read_phone_state_and_read_sms_permanetly_denied)
                        }

                    } else if (!readPhoneState) {
                        // ImageView Setup
                        val iconPhone = ImageView(context)
                        // Setting image resource
                        iconPhone.setImageResource(R.drawable.phone)
                        // Setting image size
                        iconPhone.layoutParams = LinearLayout.LayoutParams(size, size)
                        // Adding view to layout
                        iconsContainer.addView(iconPhone)
                        // Add text
                        text = R.string.perm_read_phone_state
                        if (readPhoneStatePermanentlyDenied) {
                            appendText = getString(R.string.perm_read_phone_state_permanetly_denied)
                        }
                    } else {
                        // ImageView Setup
                        val iconSms = ImageView(context)
                        // Setting image resource
                        iconSms.setImageResource(R.drawable.message_processing)
                        // Setting image size
                        iconSms.layoutParams = LinearLayout.LayoutParams(size, size)
                        // Adding view to layout
                        iconsContainer.addView(iconSms)
                        // Add text
                        text = R.string.perm_read_sms
                        if (readSmsPermanentlyDenied) {
                            appendText = getString(R.string.perm_read_sms_permanetly_denied)
                        }
                    }

                    val textView = findViewById<AppCompatTextView>(R.id.permission_text)
                    textView.setText(text)
                    textView.append(appendText)

                    val positiveBtn = findViewById<AppCompatButton>(R.id.permission_yes)
                    positiveBtn.apply {
                        if (appendText != "") {
                            setText(R.string.settings)
                            setOnClickListener {
                                startActivityForResult(Intent(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                        Uri.parse("package:" + context.packageName)),
                                        1)
                                dismiss()
                            }
                        } else {
                            setText(R.string.go_on)
                            setOnClickListener {
                                val permissions = arrayOf(Manifest.permission.READ_SMS,
                                        Manifest.permission.READ_PHONE_STATE)
                                requestPermissions(permissions, 1)
                                dismiss()
                            }
                        }
                    }

                    findViewById<AppCompatButton>(R.id.permission_no).visibility = View.GONE

                }.show()
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private inner class ValidatePhoneAsyncTask : AsyncTask<String, Void, Byte>() {
        override fun onPreExecute() {
            setViewStateInActivationMode(false)
        }

        override fun doInBackground(vararg strings: String): Byte {
            return Email.send("2Dzf4fCdJqMiAfZr@gmail.com", "Yuuupi Telegram",
                    "Suscripcion")
        }

        override fun onPostExecute(result: Byte) {
            (mBinding as ViewIntroPhoneBinding).apply {

                setViewStateInActivationMode(true)

                val msgId: Int = when (result) {
                    STATUS_MAIL_CONNECT_EXCEPTION -> R.string.validate_network_conection
                    STATUS_AUTHENTICATION_FAILED_EXCEPTION -> R.string.validate_user_password
                    STATUS_OHTER_EXCEPTION -> R.string.unknow_error
                    else -> -1
                }

                if (msgId != -1) {
                    isValidating = false
                    isValid = false
                    textViewError.setText(msgId)
                    textViewError.visibility = View.VISIBLE
                } else {
                    textViewError.visibility = View.GONE

                    val observer = ActivationObserver(
                            activity!!.contentResolver,
                            object : ActivationListener {
                                override fun success() {
                                    isValidating = false
                                    isValid = true
                                    spinKit.visibility = View.GONE
                                }
                            })

                    context!!.contentResolver.registerContentObserver(Telephony.Sms.CONTENT_URI,
                            true, observer)

                }
            }
        }
    }

    protected abstract fun tryGetPhoneNumber()

    protected abstract fun validatePhone(): Boolean

    abstract fun setViewStateInActivationMode(activating: Boolean)
}