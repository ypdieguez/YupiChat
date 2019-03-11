package com.sapp.yupi.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.sapp.yupi.data.Contact
import com.sapp.yupi.data.ContactRepository
import com.sapp.yupi.data.Conversation
import com.sapp.yupi.data.ConversationRepository
import com.sapp.yupi.utils.PhoneUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ConversationViewModel internal constructor(
        private val conversationRepo: ConversationRepository,
        private val contactRepo: ContactRepository,
        contactReadPermission: Boolean
) : ViewModel() {

    lateinit var conversations: LiveData<List<Conversation>>

    /**
     * This is the job for all coroutines started by this ViewModel.
     *
     * Cancelling this job will cancel all coroutines started by this ViewModel.
     */
    private val viewModelJob = Job()

    /**
     * This is the scope for all coroutines launched by [ConversationViewModel].
     *
     * Since we pass [viewModelJob], you can cancel all coroutines launched by [viewModelScope] by calling
     * viewModelJob.cancel().  This is called in [onCleared].
     */
    private val viewModelScope = CoroutineScope(Main + viewModelJob)

    /**
     * Cancel all coroutines when the ViewModel is cleared.
     */
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    init {
        updateConversations(contactReadPermission)
    }

    fun updateConversations(contactReadPermission: Boolean) {
        conversations = Transformations.map(conversationRepo.getConversations()) { list ->
            list.apply {
                if (contactReadPermission) {
                    forEach {
                        it.contact = contactRepo.getContactInfoForPhoneNumber(it.phone)
                    }
                } else {
                    forEach {
                        val number = PhoneUtil.toInternational(it.phone, null)
                        it.contact = Contact(number, number)
                    }
                }
            }
        }
    }

    fun delete(conversation: Conversation) {
        viewModelScope.launch {
            conversationRepo.delete(conversation)
        }
    }

    fun markConversationAsRead(phone: String) {
        viewModelScope.launch {
            conversationRepo.markConversationAsRead(phone)
        }
    }
}