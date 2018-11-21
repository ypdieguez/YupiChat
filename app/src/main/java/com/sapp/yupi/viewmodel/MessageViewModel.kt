package com.sapp.yupi.viewmodel

import androidx.lifecycle.ViewModel
import com.sapp.yupi.data.Contact
import com.sapp.yupi.data.ContactRepository
import com.sapp.yupi.data.Message
import com.sapp.yupi.data.MessageRepository

class MessageViewModel(
        private val repo: MessageRepository
) : ViewModel() {

    fun insert(message: Message) = repo.insert(message)

    fun delete(message: Message) = repo.delete(message)

    fun getMessagesForContact(contactId: Long) = repo.getMessagesForContact(contactId)
}