package com.sapp.yupi.ui.appintro


import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.text.InputType
import com.sapp.yupi.BuildConfig
import com.sapp.yupi.R
import com.sapp.yupi.util.UIUtils
import android.telephony.TelephonyManager
import android.util.Patterns
import com.sapp.yupi.databinding.ViewIntroTextInputBinding


// TODO: See how to integrate this class for read phone number
class PhoneFragment : TextInputFragment() {

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            if (UIUtils.askForPermission(activity!!, Manifest.permission.READ_PHONE_STATE,
                            this)) {
                tryPhoneNumber()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            tryPhoneNumber()
        }
    }

    @SuppressLint("MissingPermission", "HardwareIds")
    private fun tryPhoneNumber() {
        (mBinding as ViewIntroTextInputBinding).apply {
            val tMgr = context?.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            val mPhoneNumber = tMgr.line1Number
            if (mPhoneNumber.isNotEmpty() && Patterns.PHONE.matcher(mPhoneNumber).matches()) {
                textInput.setText(mPhoneNumber)
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
                PhoneFragment().apply {
                    arguments = getBundle(
                            fragmentTag = TAG_FRAGMENT_PHONE,
                            title = R.string.phone_number,
                            imageRes = R.drawable.icons8_phone_480,
                            description = R.string.intro_phone_description,
                            hint = R.string.phone_number,
                            type = InputType.TYPE_CLASS_PHONE,
                            prefix = if (BuildConfig.FLAVOR == BuildConfig.FLAVOR_WORLD) "+53" else "+1"

                    )
                }
    }
}
