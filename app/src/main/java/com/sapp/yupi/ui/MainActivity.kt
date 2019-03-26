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
import com.sapp.yupi.data.Contact
import com.sapp.yupi.databinding.ActivityMainBinding
import com.sapp.yupi.ui.appintro.IntroActivity
import com.sapp.yupi.utils.PhoneUtil
import com.sapp.yupi.UserPref
import com.sapp.yupi.ui.appintro.cuba.IntroCubaActivity
import com.sapp.yupi.ui.appintro.world.IntroWorldActivity

const val CONTACT = "contact"

class MainActivity : AppCompatActivity() {

    lateinit var mBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        UserPref.getInstance(this).apply {
            if (isReady()) {
                mBinding = DataBindingUtil.setContentView(this@MainActivity, R.layout.activity_main)
                val navController = Navigation.findNavController(this@MainActivity, R.id.nav_host_fragment)

                // Set up ActionBar
                setSupportActionBar(mBinding.toolbar)
                NavigationUI.setupActionBarWithNavController(this@MainActivity, navController)

                // Go to ConversationFragment if notification is clicked.
                intent.extras?.apply {
                    val contact = getParcelable(CONTACT) as Contact?
                    contact?.apply {
                        val phone = PhoneUtil.toE164(number, null)

                        val direction = MainFragmentDirections
                                .actionMainFragmentToConversationFragment(phone, name)
                        findNavController(R.id.nav_host_fragment).navigate(direction)
                    }
                }
            } else {
                when (from) {
                    UserPref.IN_CUBA -> startActivity(Intent(this@MainActivity, IntroCubaActivity::class.java))
                    UserPref.OUTSIDE_CUBA -> startActivity(Intent(this@MainActivity, IntroWorldActivity::class.java))
                    else -> startActivity(Intent(this@MainActivity, IntroActivity::class.java))
                }

                finish()
            }
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        hideSoftKeyboard()
        return Navigation.findNavController(this, R.id.nav_host_fragment).navigateUp()
    }

    fun setTitle(title: String) {
        mBinding.toolbar.title = title
    }

    private fun hideSoftKeyboard() {
        val view = currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }
}
