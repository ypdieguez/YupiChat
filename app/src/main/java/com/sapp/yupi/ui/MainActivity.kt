package com.sapp.yupi.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.sapp.yupi.R
import com.sapp.yupi.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        val pref = PreferenceManager.getDefaultSharedPreferences(this)
//        val nick = pref.getString(ConfigActivity.NICK, "")
//        val cell = pref.getString(ConfigActivity.CELL, "")
//        val user = pref.getString(ConfigActivity.EMAIL, "")
//        val pass = pref.getString(ConfigActivity.PASS, "")
//        if (nick.isEmpty() || cell.isEmpty() || user.isEmpty() || pass.isEmpty()) {
//            startActivity(Intent(this, ConfigActivity::class.java))
//            return
//        }

        val binding: ActivityMainBinding =
                DataBindingUtil.setContentView(this, R.layout.activity_main)

        val navController = Navigation.findNavController(this, R.id.nav_host_fragment)

        // Set up ActionBar
        setSupportActionBar(binding.toolbar)
        NavigationUI.setupActionBarWithNavController(this, navController)
    }

    override fun onSupportNavigateUp() = findNavController(R.id.nav_host_fragment).navigateUp()
}
