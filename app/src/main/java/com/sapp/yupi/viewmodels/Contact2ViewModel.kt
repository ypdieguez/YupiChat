package com.sapp.yupi.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.sapp.yupi.data.Contact2
import com.sapp.yupi.data.Contact2Repository


class Contact2ViewModel(application: Application) : ViewModel() {

    private var contactRepository = Contact2Repository(application)

    var contacts: LiveData<List<Contact2>> = contactRepository.contacts
        private set

    fun getContacts() {
        contactRepository.getContacts()
    }

    init {
        contacts = contactRepository.getContacts()
    }
}