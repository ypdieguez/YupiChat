package com.github.sapp.yupi.ui

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.github.sapp.yupi.Injector
import com.github.sapp.yupi.R
import com.github.sapp.yupi.adapter.ContactAdapter
import com.github.sapp.yupi.data.Contact
import com.github.sapp.yupi.databinding.ActivityMainBinding
import com.github.sapp.yupi.viewmodel.ContactViewModel

class MainActivity : AppCompatActivity(), ContactAdapter.Listener{

    lateinit var model : ContactViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        val nick = pref.getString(ConfigActivity.NICK, "")
        val cell = pref.getString(ConfigActivity.CELL, "")
        val user = pref.getString(ConfigActivity.EMAIL, "")
        val pass = pref.getString(ConfigActivity.PASS, "")
        if (nick.isEmpty() || cell.isEmpty() || user.isEmpty() || pass.isEmpty()) {
            startActivity(Intent(this, ConfigActivity::class.java))
            return
        }

        val binding: ActivityMainBinding =
                DataBindingUtil.setContentView(this, R.layout.activity_main)

        // Set up ActionBar
        setSupportActionBar(binding.toolbar)

        val factory = Injector.provideContactViewModelFactory(this)
        model = ViewModelProviders.of(this, factory).get(ContactViewModel::class.java)

        val adapter = ContactAdapter(this)
        binding.contactList.apply {
            this.adapter = adapter
            addOnItemTouchListener(ItemTouchListener(this@MainActivity, this))
        }

        subscribeUi(adapter, binding)

        binding.fab.setOnClickListener {
            startActivity(Intent(this, ContactActivity::class.java))
        }
    }

    private fun subscribeUi(adapter: ContactAdapter, binding: ActivityMainBinding) {
        model.getContacts().observe(this, Observer { contacts ->
            if (contacts != null && contacts.isNotEmpty()) {
                adapter.submitList(contacts)
                binding.hasContacts = true
            } else {
                binding.hasContacts = false
            }
        })
    }

    override fun delete(contact: Contact) {
        model.delete(contact)
    }
}
