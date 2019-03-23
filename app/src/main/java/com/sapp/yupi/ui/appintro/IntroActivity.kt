package com.sapp.yupi.ui.appintro

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.sapp.yupi.R
import com.sapp.yupi.databinding.ViewIntroBinding
import com.sapp.yupi.ui.appintro.cuba.IntroCubaActivity
import com.sapp.yupi.ui.appintro.world.IntroWorldActivity

class IntroActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        DataBindingUtil.setContentView<ViewIntroBinding>(this, R.layout.view_intro).apply {
            fromCuba.setOnClickListener {
                startActivity(Intent(this@IntroActivity, IntroCubaActivity::class.java))
            }

            outsideCuba.setOnClickListener {
                startActivity(Intent(this@IntroActivity, IntroWorldActivity::class.java))
            }
        }
    }
}
