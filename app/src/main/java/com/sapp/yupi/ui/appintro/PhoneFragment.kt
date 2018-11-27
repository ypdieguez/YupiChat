package com.sapp.yupi.ui.appintro


import android.Manifest
import android.app.Dialog
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import com.sapp.yupi.BuildConfig
import com.sapp.yupi.R


class PhoneFragment : TextInputFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        if (shouldShowRequestPermissionRationale(Manifest.permission.READ_PHONE_STATE)){
//        }
        super.onViewCreated(view, savedInstanceState)

        val dialog = Dialog(context!!)
        dialog.setContentView(R.layout.permission_dialog)
//        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()

    }

    companion object {
        @JvmStatic
        fun newInstance() =
                PhoneFragment().apply {
                    arguments = getBundle(
                            R.layout.view_intro_text_input,
                            TAG_FRAGMENT_PHONE,
                            R.string.phone_number,
                            R.drawable.icons8_phone_480,
                            R.string.intro_phone_description
                    ).apply {
                        putInt(ARG_HINT, R.string.phone_number)
                        putInt(ARG_TYPE, InputType.TYPE_CLASS_PHONE)
                        putString(ARG_PREFIX, if (BuildConfig.FLAVOR == BuildConfig.FLAVOR_WORLD) "+53" else "+1")
                    }
                }
    }
}
