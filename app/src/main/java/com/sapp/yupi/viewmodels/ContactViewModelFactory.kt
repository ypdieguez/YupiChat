package com.sapp.yupi.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sapp.yupi.data.ContactRepository

/**
 * Factory for creating a [ContactViewModel] with a constructor that takes a [ContactRepository].
 */
class ContactViewModelFactory(private val contactRepo: ContactRepository)
    : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>) =
            ContactViewModel(contactRepo) as T
}