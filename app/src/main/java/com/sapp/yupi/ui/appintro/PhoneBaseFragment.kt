package com.sapp.yupi.ui.appintro

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat
import com.google.i18n.phonenumbers.PhoneNumberUtil.MatchType
import com.google.i18n.phonenumbers.Phonenumber
import com.sapp.yupi.*
import com.sapp.yupi.databinding.ViewIntroPhoneBinding
import com.sapp.yupi.utils.*

const val TAG_FRAGMENT_PHONE = "fragment_phone"

abstract class PhoneBaseFragment : IntroFragment() {

    private val receiver = object : BroadcastReceiver() {
        private var isRegistered: Boolean = false

        override fun onReceive(context: Context, intent: Intent) {
            activity?.apply {
                val phone = intent.getStringExtra(PHONE_NOTIFICATION)

                // Utils
                val user = UserInfo.getInstance(context)
                val phoneUtil = PhoneNumberUtil.getInstance()

                if (isValidating && phoneUtil.isNumberMatch(phone, user.phone)
                        == PhoneNumberUtil.MatchType.EXACT_MATCH) {
                    isValidating = false
                    isValidated = true

                    // UserInfo is too updated in IncomingMsgWorker
                    user.phoneValidated = true

                    runOnUiThread {
                        setViewStateInActivationMode(true)
                        goToNextSlide()
                    }

                    abortBroadcast()
                    unregister(context)
                }
            }
        }

        fun register(context: Context) {
            if (!isRegistered) {
                val filter = IntentFilter(BROADCAST_NOTIFICATION)
                filter.priority = 1
                context.registerReceiver(this, filter)

                isRegistered = true
            }
        }

        fun unregister(context: Context) {
            if (isRegistered) {
                context.unregisterReceiver(this)

                isRegistered = false
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        UserInfo.getInstance(context!!).apply {
            isValidated = phoneValidated
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        receiver.unregister(requireContext())
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            doRequest()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray) {
        if (requestCode == REQUEST_CODE_READ_SMS) doRequest()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        doRequest()
    }

    override fun isPolicyRespected(): Boolean {
        if (!isValidating) {
            showError(false)
            if (isValidPhone() && !isValidated && isNetworkConnected()) {
                ValidatePhoneAsyncTask().execute()
                isValidating = true
            }
        }

        return isValidated
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

    protected abstract fun isValidPhone(): Boolean

    protected fun updateUserInfo(phoneNumber: Phonenumber.PhoneNumber) {
        val phoneUtil = PhoneNumberUtil.getInstance()
        UserInfo.getInstance(context!!).apply {
            val matchType = phoneUtil.isNumberMatch(phoneNumber, phone)
            if (matchType == MatchType.NO_MATCH || matchType == MatchType.NOT_A_NUMBER) {
                // Save to Preferences
                phone = phoneUtil.format(phoneNumber, PhoneNumberFormat.E164)
                phoneValidated = false

                isValidated = false
            }
        }
    }

    private fun doRequest() {
        if (!askForReadSmsPermission())
            tryGetPhoneNumber()
    }

    private fun askForReadSmsPermission(): Boolean {
        requireContext().let { context ->
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_SMS) !=
                    PackageManager.PERMISSION_GRANTED) {

                val pref = context.getSharedPreferences(PERMISSION_PREFERENCES, Context.MODE_PRIVATE)
                val isPermanentlyDenied = pref.run {
                    val isPermanentlyDenied =
                            getBoolean(PREF_READ_SMS_PERMISSION_ASKED, false)
                                    && !shouldShowRequestPermissionRationale(Manifest.permission.READ_SMS)

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

                    // permanently
                    val appendText = when (isPermanentlyDenied) {
                        true -> String.format(getString(R.string.perm_permanently_denied),
                                getString(R.string.sms))
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
                                        REQUEST_CODE_READ_SMS)
                                dismiss()
                            }
                        } else {
                            setText(R.string.go_on)
                            setOnClickListener {
                                requestPermissions(arrayOf(Manifest.permission.READ_SMS),
                                        REQUEST_CODE_READ_SMS)
                                pref.edit {
                                    putBoolean(PREF_READ_SMS_PERMISSION_ASKED, true)
                                }
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
        }

        override fun doInBackground(vararg strings: String): Byte {
            val phone = UserInfo.getInstance(context!!).phone
            return Email.getInstance(context!!).send(BuildConfig.RECIPIENT_EMAIL, phone,
                    getString(R.string.subscription))
        }

        override fun onPostExecute(result: Byte) {
            (mBinding as ViewIntroPhoneBinding).apply {
                val msgId: Int = when (result) {
                    STATUS_MAIL_CONNECT_EXCEPTION -> R.string.host_not_connected
                    STATUS_AUTHENTICATION_FAILED_EXCEPTION -> R.string.wrong_user_or_password
                    STATUS_OTHER_EXCEPTION -> R.string.unknown_error
                    else -> -1
                }

                if (msgId != -1) {
                    isValidating = false
                    isValidated = false
                    extraFields.apply {
                        textViewError.setText(msgId)
                        textViewError.visibility = View.VISIBLE
                    }
                    setViewStateInActivationMode(true)
                } else {
                    extraFields.textViewError.visibility = View.GONE

                    // Wait for a notification in receiver
                    receiver.register(requireContext())
                }
            }
        }
    }

    companion object {
        const val REQUEST_CODE_READ_SMS = 1
    }
}