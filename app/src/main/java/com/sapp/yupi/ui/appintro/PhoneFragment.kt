package com.sapp.yupi.ui.appintro


import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.InputType
import android.view.View
import com.sapp.yupi.BuildConfig
import com.sapp.yupi.R


class PhoneFragment : TextInputFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        if (shouldShowRequestPermissionRationale(Manifest.permission.READ_PHONE_STATE)){
//            requestPermissions(arrayOf(Manifest.permission.READ_PHONE_STATE), 1)
//        }
        super.onViewCreated(view, savedInstanceState)

        val dialog = Dialog(context!!)
        dialog.setContentView(R.layout.permission_dialog)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
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
