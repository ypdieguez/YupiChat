package com.sapp.yupi.util

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.WindowManager
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.sapp.yupi.R

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

        fun checkPermission(context: Context) = ContextCompat.checkSelfPermission(context,
                Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED

        fun askForPermission(context: Context, permission: String, listener: Fragment): Boolean {
            if(ContextCompat.checkSelfPermission(context, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                val dialog = Dialog(context)
                dialog.setContentView(R.layout.permission_dialog)
                dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                dialog.setCanceledOnTouchOutside(false)

                val icon: Int
                val text: Int
                val btn: Int

                when(permission) {
                    Manifest.permission.READ_PHONE_STATE -> {
                        icon = R.drawable.phone
                        text = R.string.permission_read_phone_state
                        btn = R.string.go_on
                    }
                    else -> {
                        icon = -1
                        text = -1
                        btn = -1
                    }
                }

                dialog.findViewById<AppCompatImageView>(R.id.permission_icon).setImageResource(icon)
                dialog.findViewById<AppCompatTextView>(R.id.permission_text).setText(text)
                dialog.findViewById<AppCompatButton>(R.id.permission_yes).apply {
                    setText(btn)
                    setOnClickListener {
                        listener.requestPermissions(arrayOf(permission), 1)
                        dialog.dismiss()
                    }
                }
                dialog.findViewById<AppCompatButton>(R.id.permission_no).setOnClickListener {
                    dialog.dismiss()
                }

                dialog.show()

                return false
            }
            return true
        }
    }
}