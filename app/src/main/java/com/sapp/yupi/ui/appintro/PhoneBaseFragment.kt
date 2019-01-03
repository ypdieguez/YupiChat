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

const val TAG_FRAGMENT_PHONE = "fragment_phone"

abstract class PhoneBaseFragment : IntroFragment() {

    private lateinit var observer: ValidatePhoneObserver
    private lateinit var validateMsg: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        context?.apply {
            observer = ValidatePhoneObserver(contentResolver)
        }
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            doRequest()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray) {
        doRequest()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        doRequest()
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
        (mBinding as ViewIntroPhoneBinding).extraFields.apply {
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

    private fun doRequest() {
        if (!askForReadSmsPermission())
            tryGetPhoneNumber()
    }

    private fun askForReadSmsPermission(): Boolean {
        context?.let { context ->
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_SMS) !=
                    PackageManager.PERMISSION_GRANTED) {

                val isPermanentlyDenied = context.getSharedPreferences(PERMISSION_PREFERENCES,
                        Context.MODE_PRIVATE).run {
                    val isPermanentlyDenied =
                            !getBoolean(PREF_FIRST_TIME_ASKED_READ_SMS_PERMISSION, true)
                            && !shouldShowRequestPermissionRationale(Manifest.permission.READ_SMS)

                    edit {
                        putBoolean(PREF_FIRST_TIME_ASKED_READ_SMS_PERMISSION, false)
                    }

                    isPermanentlyDenied
                }

                val dialog = Dialog(context)
                dialog.apply {
                    setContentView(R.layout.permission_dialog)
                    window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    setCanceledOnTouchOutside(false)
                    setCancelable(false)

                    // ImageView Setup
                    val iconSms = ImageView(context)
                    // Setting image resource
                    iconSms.setImageResource(R.drawable.message_processing)
                    // Setting image size
                    val size = resources.getDimensionPixelSize(R.dimen.icon)
                    iconSms.layoutParams = LinearLayout.LayoutParams(size, size)
                    // Adding view to layout
                    findViewById<LinearLayout>(R.id.icons_container).addView(iconSms)

                    val appendText = when (isPermanentlyDenied) {
                        true -> getString(R.string.perm_read_sms_permanetly_denied)
                        false -> ""
                    }

                    val textView = findViewById<AppCompatTextView>(R.id.permission_text)
                    textView.text = String.format(getString(R.string.perm_read_sms),
                            getString(R.string.app_name))
                    textView.append(" $appendText")

                    findViewById<AppCompatButton>(R.id.permission_yes).apply {
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
                                requestPermissions(arrayOf(Manifest.permission.READ_SMS),
                                        1)
                                dismiss()
                            }
                        }
                    }

                    findViewById<AppCompatButton>(R.id.permission_no).visibility = View.GONE
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
            validateMsg = getString(R.string.validate_phone) + " " + UserPrefUtil.getPhone()
        }

        override fun doInBackground(vararg strings: String): Byte {
            return Email.send("gtom20180828@gmail.com", UserPrefUtil.getPhone(), validateMsg)
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
                    extraFields.apply {
                        textViewError.setText(msgId)
                        textViewError.visibility = View.VISIBLE
                    }
                    setViewStateInActivationMode(true)
                } else {
                    extraFields.textViewError.visibility = View.GONE
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
            if (body.contains(validateMsg)) {
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
            }
        }
    }
}