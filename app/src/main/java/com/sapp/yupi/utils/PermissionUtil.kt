package com.sapp.yupi.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

class PermissionUtil {
    companion object {
        fun hasReadContactPermission(context: Context) =
                ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) ==
                        PackageManager.PERMISSION_GRANTED

        fun hasSmsPermission(context: Context) =
                ContextCompat.checkSelfPermission(context, Manifest.permission.READ_SMS) ==
                        PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                        context, Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED
    }
}