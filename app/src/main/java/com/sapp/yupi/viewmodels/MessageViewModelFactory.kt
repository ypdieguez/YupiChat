package com.sapp.yupi.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sapp.yupi.data.MessageRepository

/**
 * Factory for creating a [MessageViewModel] with a constructor that takes a [MessageRepository].
 */
class MessageViewModelFactory(
        private val repo: MessageRepository
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>) = MessageViewModel(repo) as T
}