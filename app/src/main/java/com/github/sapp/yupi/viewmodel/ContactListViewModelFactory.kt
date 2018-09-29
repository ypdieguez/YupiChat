package com.github.sapp.yupi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.sapp.yupi.data.ContactRepository

/**
 * Factory for creating a [ContactListViewModel] with a constructor that takes a [ContactRepository].
 */
class ContactListViewModelFactory(
        private val repository: ContactRepository
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>) = ContactListViewModel(repository) as T
}