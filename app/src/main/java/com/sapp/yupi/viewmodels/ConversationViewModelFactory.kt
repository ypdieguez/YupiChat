package com.sapp.yupi.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sapp.yupi.data.ContactRepository
import com.sapp.yupi.data.ConversationRepository

/**
 * Factory for creating a [ConversationViewModel] with a constructor that takes a
 * [ConversationRepository].and [ContactRepository]
 */
class ConversationViewModelFactory(
        private val conversationRepo: ConversationRepository,
        private val contactRepo: ContactRepository,
        private val contactReadPermission: Boolean
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>) =
            ConversationViewModel(conversationRepo, contactRepo, contactReadPermission) as T
}