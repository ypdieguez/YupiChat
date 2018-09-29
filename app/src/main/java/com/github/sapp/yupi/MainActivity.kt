package com.github.sapp.yupi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.github.sapp.yupi.adapter.ContactAdapter
import com.github.sapp.yupi.databinding.ActivityMainBinding
import com.github.sapp.yupi.viewmodel.ContactListViewModel

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: ActivityMainBinding =
                DataBindingUtil.setContentView(this, R.layout.activity_main)

        // Set up ActionBar
        setSupportActionBar(binding.toolbar)

        val adapter = ContactAdapter()
        binding.recyclerView.adapter = adapter

        subscribeUi(adapter, binding)
    }

    private fun subscribeUi(adapter: ContactAdapter, binding: ActivityMainBinding) {
        val factory = Injector.provideContactListViewModelFactory(this)
        val model = ViewModelProviders.of(this, factory).get(ContactListViewModel::class.java)

        model.getContacts().observe(this, Observer{ contacts ->
            if (contacts != null && contacts.isNotEmpty()) {
                adapter.submitList(contacts)
                binding.hasContacts = true
            } else {
                binding.hasContacts = false
            }
        })
    }
}
