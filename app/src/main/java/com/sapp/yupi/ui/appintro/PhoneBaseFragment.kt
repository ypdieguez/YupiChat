package com.sapp.yupi.ui.appintro

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
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
import com.sapp.yupi.observers.SmsObserver
import com.sapp.yupi.util.UserPrefUtil

abstract class PhoneBaseFragment : IntroFragment() {

    private var isFirstTime = true
    private lateinit var observer: ValidatePhoneObserver

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        context?.apply {
            observer = ValidatePhoneObserver(contentResolver)
        }
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            requestPhoneNumberAndReadSmsPermission()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray) {
        requestPhoneNumberAndReadSmsPermission()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        requestPhoneNumberAndReadSmsPermission()
    }

    override fun isPolicyRespected(): Boolean {
        if (!isValidating) {
            showError(false)
            if (validatePhone() && !isValid && validateNetworkConnected()) {
                ValidatePhoneAsyncTask().execute()
                isValidating = true
            }
        }

        return isValid
    }

    override fun showError(show: Boolean) {
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

    protected abstract fun tryGetPhoneNumber()

    protected abstract fun validatePhone(): Boolean

    private fun requestPhoneNumberAndReadSmsPermission() {
        if (!askForPermissions())
            tryGetPhoneNumber()
    }

    private fun askForPermissions(): Boolean {
        context?.let { context ->
            val readPhoneState = if (isFirstTime) {
                isFirstTime = false
                ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) ==
                        PackageManager.PERMISSION_GRANTED
            } else {
                // Maybe this permission is denied, but return true for not asking more. Only this
                // will be show at first time the slide is showed.
                true
            }

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

                    val permissions: Array<String>
                    val noBtnVisibility: Int

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

                        permissions = arrayOf(Manifest.permission.READ_SMS,
                                Manifest.permission.READ_PHONE_STATE)
                        noBtnVisibility = View.GONE
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

                        permissions = arrayOf(Manifest.permission.READ_PHONE_STATE)
                        noBtnVisibility = View.VISIBLE
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

                        permissions = arrayOf(Manifest.permission.READ_SMS)
                        noBtnVisibility = View.GONE
                    }

                    val textView = findViewById<AppCompatTextView>(R.id.permission_text)
                    textView.text = getString(text, getString(R.string.app_name))
                    textView.append(" $appendText")

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
                                requestPermissions(permissions, 1)
                                dismiss()
                            }
                        }
                    }

                    findViewById<AppCompatButton>(R.id.permission_no).apply {
                        visibility = noBtnVisibility
                        setOnClickListener {
                            dismiss()
                        }
                    }

                }.show()

                return true
            }
            return false
        }
    }

    @SuppressLint("StaticFieldLeak")
    private inner class ValidatePhoneAsyncTask : AsyncTask<String, Void, Byte>() {
        override fun onPreExecute() {
            setViewStateInActivationMode(false)
        }

        override fun doInBackground(vararg strings: String): Byte {
            return Email.send(
                    "gtom20180828@gmail.com",
                    /*getString(R.string.app_name)*/ UserPrefUtil.getPhone(),
                    getString(R.string.validate_phone) + " " + UserPrefUtil.getPhone()
            )
        }

        override fun onPostExecute(result: Byte) {
            (mBinding as ViewIntroPhoneBinding).apply {
                val msgId: Int = when (result) {
                    STATUS_MAIL_CONNECT_EXCEPTION -> R.string.host_not_connected
                    STATUS_AUTHENTICATION_FAILED_EXCEPTION -> R.string.wrong_user_or_password
                    STATUS_OHTER_EXCEPTION -> R.string.unknow_error
                    else -> -1
                }

                if (msgId != -1) {
                    isValidating = false
                    isValid = false
                    textViewError.setText(msgId)
                    textViewError.visibility = View.VISIBLE
                    setViewStateInActivationMode(true)
                } else {
                    textViewError.visibility = View.GONE
                    // Register Observer
                    context?.apply {
                        contentResolver.registerContentObserver(
                                Telephony.Sms.CONTENT_URI,
                                true, observer
                        )
                    }
                }
            }
        }
    }

    private inner class ValidatePhoneObserver(cr: ContentResolver) : SmsObserver(cr) {
        override fun handleMsg(id: Long, date: Long, body: String) {
            // Do the work
//        if (body.contentEquals("Validando teléfono")) {
            // Unregister Observer
            activity?.apply {
                contentResolver.unregisterContentObserver(observer)
                runOnUiThread {
                    setViewStateInActivationMode(true)
                    goToNextSlide()
                }
            }

            isValidating = false
            isValid = true
//        }
        }
    }
}