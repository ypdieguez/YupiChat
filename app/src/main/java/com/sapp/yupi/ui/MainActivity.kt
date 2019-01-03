package com.sapp.yupi.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.sapp.yupi.R
import com.sapp.yupi.databinding.ActivityMainBinding
import com.sapp.yupi.ui.appintro.IntroActivity
import com.sapp.yupi.util.UserPrefUtil

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!UserPrefUtil.check()) {
            startActivity(Intent(this, IntroActivity::class.java))
            finish()
        } else {

            val binding: ActivityMainBinding =
                    DataBindingUtil.setContentView(this, R.layout.activity_main)

            val navController = Navigation.findNavController(this, R.id.nav_host_fragment)

            // Set up ActionBar
            setSupportActionBar(binding.toolbar)
            NavigationUI.setupActionBarWithNavController(this, navController)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        hideKeyboard()
        return findNavController(R.id.nav_host_fragment).navigateUp()
    }

    private fun hideKeyboard() {
        val view = currentFocus
        if (view != null) {
            val inputMethodManager =
                    getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}
