package com.github.sapp.yupi

import android.content.Context
import com.github.sapp.yupi.data.AppDatabase
import com.github.sapp.yupi.data.ContactRepository
import com.github.sapp.yupi.viewmodel.ContactListViewModelFactory
import com.github.sapp.yupi.viewmodel.ContactViewModelFactory

/**
 * Static methods used to inject classes needed for various Activities and Fragments.
 */
object Injector {

    fun provideContactListViewModelFactory(context: Context): ContactListViewModelFactory {
        val repository = getContactRepository(context)
        return ContactListViewModelFactory(repository)
    }

    fun provideContactViewModelFactory(context: Context): ContactViewModelFactory {
        val repository = getContactRepository(context)
        return ContactViewModelFactory(repository)
    }

    private fun getContactRepository(context: Context): ContactRepository {
        return ContactRepository.getInstance(AppDatabase.getInstance(context).contactDao())
    }

}