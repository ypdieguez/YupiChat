package com.sapp.yupi.ui.appintro

import android.os.Bundle
import android.provider.Telephony
import android.view.View
import com.sapp.yupi.databinding.ViewIntroActivationBinding
import com.sapp.yupi.observers.ActivationListener
import com.sapp.yupi.observers.ActivationObserver

class ActivateFragment : IntroFragment() {

    private var success = false
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (mBinding as ViewIntroActivationBinding).apply {
            val contentResolver = context!!.contentResolver
            btn.setOnClickListener {
                val observer = ActivationObserver(
                        contentResolver,
                        object: ActivationListener {
                            override fun success() {
                               success = true

                            }
                        })
                contentResolver.registerContentObserver(Telephony.Sms.CONTENT_URI,
                        true, observer)
            }
        }
    }
}