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
import com.sapp.yupi.utils.MessageNotification
import com.sapp.yupi.utils.PhoneUtil
import com.sapp.yupi.utils.UserInfo

const val CONTACT = "contact"

class MainActivity : AppCompatActivity() {

    lateinit var mBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (UserInfo.getInstance(this).isReady()) {
            mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

            val navController = Navigation.findNavController(this, R.id.nav_host_fragment)

            // Set up ActionBar
            setSupportActionBar(mBinding.toolbar)
            NavigationUI.setupActionBarWithNavController(this, navController)

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
            startActivity(Intent(this, IntroActivity::class.java))
            finish()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        hideKeyboard()
        return Navigation.findNavController(this, R.id.nav_host_fragment).navigateUp()
    }

    fun setTitle(title: String) {
        mBinding.toolbar.title = title
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
