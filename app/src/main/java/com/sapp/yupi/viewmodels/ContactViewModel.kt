package com.sapp.yupi.viewmodels

import androidx.lifecycle.ViewModel
import com.sapp.yupi.data.Contact
import com.sapp.yupi.data.ContactRepository

class ContactViewModel(
        private val repo: ContactRepository
) : ViewModel() {

    fun getContacts() = repo.getContacts()

    fun getContact(id: Long) = repo.getContact(id)

    fun insert(contact: Contact) = repo.insert(contact)

    fun update(contact: Contact) = repo.update(contact)

    fun delete(contact: Contact) = repo.delete(contact)
}