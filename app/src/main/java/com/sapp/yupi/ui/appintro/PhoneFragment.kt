package com.sapp.yupi.ui.appintro


import android.Manifest
import android.app.Dialog
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.sapp.yupi.BuildConfig
import com.sapp.yupi.R


class PhoneFragment : TextInputFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (shouldShowRequestPermissionRationale(Manifest.permission.READ_PHONE_STATE)){
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = TextInputFragment.newInstance(
                fragmentTag = TAG_FRAGMENT_PHONE,
                title = R.string.phone_number,
                imageRes = R.drawable.icons8_phone_480,
                description = R.string.intro_phone_description,
                hint = R.string.phone_number,
                type = InputType.TYPE_CLASS_PHONE,
                prefix = if (BuildConfig.FLAVOR == BuildConfig.FLAVOR_WORLD) "+53" else "+1")
                as PhoneFragment
    }
}
