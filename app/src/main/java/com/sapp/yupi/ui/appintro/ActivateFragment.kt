package com.sapp.yupi.ui.appintro

import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.MessageQueue
import android.provider.Telephony
import android.view.View
import com.sapp.yupi.Mail
import com.sapp.yupi.R
import com.sapp.yupi.STATUS_SUCCESS
import com.sapp.yupi.databinding.ViewIntroActivationBinding
import com.sapp.yupi.observers.ActivationListener
import com.sapp.yupi.observers.ActivationObserver
import kotlin.concurrent.thread


class ActivateFragment : IntroFragment() {

    private var success = false
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (mBinding as ViewIntroActivationBinding).apply {
            val contentResolver = context!!.contentResolver
            btn.setOnClickListener {

                it.visibility = View.GONE
                error.visibility = View.GONE
                timer.visibility = View.VISIBLE

                thread {
                    val context = context!!
                    val pref = context.getSharedPreferences(USER_PREFERENCES, Context.MODE_PRIVATE)
                    val status = Mail.send(context, "gtom20180828@gmail.com", "", "")

                    if (status != STATUS_SUCCESS) {
                        Handler().post {
                            timer.visibility = View.GONE
                            it.visibility = View.VISIBLE
                            error.visibility = View.VISIBLE
                            error.text = "Error"
                        }
                    }

                }

                val countDownTimer = object : CountDownTimer(
                        5 * 60 * 1000, 1000) {
                    override fun onTick(millisUntilFinished: Long) {
                        var seconds = (millisUntilFinished / 1000)
                        val minutes = seconds / 60
                        seconds %= 60

                        timer.text = String.format("%d:%02d", minutes, seconds)
                    }

                    override fun onFinish() {
                        timer.visibility = View.GONE
                        btn.visibility = View.VISIBLE
                        error.visibility = View.VISIBLE
                        error.setText("Intentalo de nuevo")
                    }
                }

                val observer = ActivationObserver(
                        contentResolver,
                        object : ActivationListener {
                            override fun success() {
                                success = true
                                timer.visibility = View.GONE
                                description.text = "Activado"
                                countDownTimer.cancel()
                            }
                        })

                contentResolver.registerContentObserver(Telephony.Sms.CONTENT_URI,
                        true, observer)

                countDownTimer.start()
            }
        }
    }

    override fun isPolicyRespected(): Boolean {
        return success
    }

    override fun onUserIllegallyRequestedNextPage() {
        (mBinding as ViewIntroActivationBinding).apply {
            error.text = "Must be activate"
        }
    }


    companion object {
        @JvmStatic
        fun newInstance() = ActivateFragment().apply {
            arguments = getBundle(R.layout.view_intro_activation, "fragment_activate")
        }
    }
}