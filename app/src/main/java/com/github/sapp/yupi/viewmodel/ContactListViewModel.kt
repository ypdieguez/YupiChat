package com.github.sapp.yupi.viewmodel

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.github.sapp.yupi.data.Contact
import com.github.sapp.yupi.data.ContactRepository

class ContactListViewModel internal constructor(
        repository: ContactRepository
) : ViewModel() {

    private val contactList = MediatorLiveData<List<Contact>>()

    init {
        contactList.addSource(repository.getContacts(), contactList::setValue)
    }

    fun getContacts() = contactList
}