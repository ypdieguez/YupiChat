package com.sapp.yupi.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.sapp.yupi.R
import com.sapp.yupi.databinding.ActivityMainBinding
import com.sapp.yupi.ui.appintro.IntroActivity
import com.sapp.yupi.ui.appintro.USER_PREFERENCES
import com.sapp.yupi.util.UIUtils


const val FIRST_LAUNCH = "first_launch"

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val pref = getSharedPreferences(USER_PREFERENCES, Context.MODE_PRIVATE)

        if(!pref.getBoolean(FIRST_LAUNCH, true)) {
            startActivity(Intent(this, IntroActivity::class.java))
            finish()
        } else if (!UIUtils.checkPermission(this)) {
            
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
