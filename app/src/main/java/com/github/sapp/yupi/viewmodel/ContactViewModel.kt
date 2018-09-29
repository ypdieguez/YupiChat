package com.github.sapp.yupi.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.github.sapp.yupi.data.Contact
import com.github.sapp.yupi.data.ContactDao
import com.github.sapp.yupi.data.ContactRepository

class ContactViewModel(
        contactRepository: ContactRepository,
        id: Int
) : ViewModel() {
    var contact: LiveData<Contact> = contactRepository.getContact(id)
}