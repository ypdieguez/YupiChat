package com.sapp.yupi.util

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.view.WindowManager
import androidx.core.content.ContextCompat

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
    }
}