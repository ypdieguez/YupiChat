package com.sapp.yupi

import android.content.Context
import com.sapp.yupi.data.AppDatabase
import com.sapp.yupi.data.ContactRepository
import com.sapp.yupi.data.MessageRepository
import com.sapp.yupi.viewmodels.ContactViewModelFactory
import com.sapp.yupi.viewmodels.MessageViewModelFactory

/**
 * Static methods used to inject classes needed for various Activities and Fragments.
 */
object Injector {

    fun provideContactViewModelFactory(context: Context): ContactViewModelFactory {
        val repository = getContactRepository(context)
        return ContactViewModelFactory(repository)
    }

    private fun getContactRepository(context: Context): ContactRepository {
        return ContactRepository.getInstance(AppDatabase.getInstance(context).contactDao())
    }

    fun provideMessageViewModelFactory(context: Context): MessageViewModelFactory {
        val repository = getMessageRepository(context)
        return MessageViewModelFactory(repository)
    }

    private fun getMessageRepository(context: Context): MessageRepository {
        return MessageRepository.getInstance(AppDatabase.getInstance(context).messageDao())
    }

}