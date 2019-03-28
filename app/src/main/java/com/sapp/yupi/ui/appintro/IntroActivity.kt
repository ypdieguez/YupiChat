package com.sapp.yupi.ui.appintro

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.sapp.yupi.R
import com.sapp.yupi.UserPref
import com.sapp.yupi.databinding.ViewIntroBinding
import com.sapp.yupi.ui.appintro.cuba.IntroCubaActivity
import com.sapp.yupi.ui.appintro.world.IntroWorldActivity

class IntroActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        UserPref.getInstance(this).apply {
            from = ""

            email = ""
            pass = ""
            phone = ""

            emailValidated = false
            passValidated = false
            phoneValidated = false

            host = ""
            port = ""
            sslEnabled = ""
        }

        DataBindingUtil.setContentView<ViewIntroBinding>(this, R.layout.view_intro).apply {

            fromCuba.setOnClickListener {
                startActivity(IntroCubaActivity::class.java)
            }

            outsideCuba.setOnClickListener {
                startActivity(IntroWorldActivity::class.java)
            }
        }
    }

    private fun startActivity(cls: Class<*>) {
        startActivity(
                Intent(this, cls).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK))
        finish()
    }
}
