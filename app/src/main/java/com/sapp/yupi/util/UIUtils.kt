package com.sapp.yupi.util

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.provider.Settings
import android.view.WindowManager
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import com.sapp.yupi.R

private const val PERMISSION_PREFERENCES = "permissions_preferences"
private const val PREF_FIRST_READ_PHONE_STATE_PERMISSION = "first_read_phone_state_permission"

class UIUtils {

    companion object {

        /**
         * Sets the required flags on the dialog window to enable input method window to show up.
         */
        fun requestInputMethod(activity: Activity) {
            activity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        }

        /**
         * Sets the required flags on the dialog window to disable input method window to hide up.
         */
        fun releaseInputMethod(activity: Activity) {
            activity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        }

        /**
         * Check READ_SMS permission.
         */
        fun checkReadSmsPermission(context: Context) = ContextCompat.checkSelfPermission(context,
                Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED

        /**
         * Check READ_PHONE_STATE permission.
         */
        fun checkReadPhoneStatePermission(context: Context) = ContextCompat.checkSelfPermission(
                context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED

        /**
         * Show a dialog like WhatsApp for ask permissions.
         *
         * @return Boolean
         */
        fun askForPermission(permission: String, listener: Fragment): Boolean {
            val activity = listener.activity!!
            if (ContextCompat.checkSelfPermission(activity, permission)
                    != PackageManager.PERMISSION_GRANTED) {

                val firstTime: Boolean = activity.getSharedPreferences(PERMISSION_PREFERENCES,
                        Context.MODE_PRIVATE).run {
                    val r = getBoolean(PREF_FIRST_READ_PHONE_STATE_PERMISSION,
                        true)
                    if (r) edit { putBoolean(PREF_FIRST_READ_PHONE_STATE_PERMISSION, false) }

                    r
                }

                val icon: Int
                val text: Int
                val appendText: String

                when (permission) {
                    Manifest.permission.READ_PHONE_STATE -> {
                        icon = R.drawable.phone
                        text = R.string.permission_read_phone_state
                        appendText = activity.getString(R.string.tap_settings_phone)
                    }
                    else -> {
                        icon = -1
                        text = -1
                        appendText = ""
                    }
                }

                val dialog = Dialog(activity)
                dialog.apply {
                    setContentView(R.layout.permission_dialog)
                    window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    setCanceledOnTouchOutside(false)

                    findViewById<AppCompatImageView>(R.id.permission_icon).setImageResource(icon)

                    val permText = findViewById<AppCompatTextView>(R.id.permission_text)
                    val btnYes = findViewById<AppCompatButton>(R.id.permission_yes)

                    permText.setText(text)
                    if (!firstTime && !listener.shouldShowRequestPermissionRationale(permission)) {
                        permText.append(appendText)
                        btnYes.setText(R.string.settings)
                        btnYes.setOnClickListener {
                            listener.startActivityForResult(Intent(
                                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                    Uri.parse("package:" + activity.packageName)),
                                    1)
                            dismiss()
                        }
                    } else {
                        btnYes.setText(R.string.go_on)
                        btnYes.setOnClickListener {
                            listener.requestPermissions(arrayOf(permission), 1)
                            dismiss()
                        }
                    }

                    findViewById<AppCompatButton>(R.id.permission_no).setOnClickListener {
                        dismiss()
                    }
                }.show()

                return false
            }
            return true
        }
    }
}