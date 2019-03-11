package com.sapp.yupi.viewmodels

import androidx.lifecycle.ViewModel
import com.sapp.yupi.data.ContactRepository

class ContactViewModel(private val repo: ContactRepository) : ViewModel() {
    var contacts = repo.getContacts()

    fun refreshContacts() {
            contacts = repo.getContacts()
    }
}