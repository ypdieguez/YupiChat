package com.sapp.yupi.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sapp.yupi.data.ConversationRepository

/**
 * Factory for creating a [ConversationViewModel] with a constructor that takes a [PlantRepository].
 */
class ConversationViewModelFactory(
        private val repository: ConversationRepository
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>) = ConversationViewModel(repository) as T
}