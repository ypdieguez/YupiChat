package com.sapp.yupi.viewmodels

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.sapp.yupi.data.Contact2Repository
import com.sapp.yupi.data.Conversation
import com.sapp.yupi.data.ConversationRepository

class ConversationViewModel internal constructor(
        private val conversationRepository: ConversationRepository,
        private val contactRepository: Contact2Repository
) : ViewModel() {
    private val conversationList = MediatorLiveData<List<Conversation>>()

    init {
        val liveConversationList = Transformations.switchMap(conversationRepository.getConversations()) { convList ->
            val list = contactRepository.contacts.value

            convList.forEach { conv ->
                list.forEach { contact ->
                    if (contact.number == conv.phone) {
                        conv.contact = contact
                    }
                }
            }
            conversationList
        }

    }
    conversationList.addSource(liveConversationList, conversationList::setValue)
}
}