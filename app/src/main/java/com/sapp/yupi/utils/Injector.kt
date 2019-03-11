package com.sapp.yupi.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.sapp.yupi.data.AppDatabase
import com.sapp.yupi.data.ContactRepository
import com.sapp.yupi.data.ConversationRepository
import com.sapp.yupi.data.MessageRepository
import com.sapp.yupi.viewmodels.ContactViewModelFactory
import com.sapp.yupi.viewmodels.ConversationViewModelFactory
import com.sapp.yupi.viewmodels.MessageViewModelFactory

/**
 * Static methods used to inject classes needed for various Activities and Fragments.
 */
object Injector {

    fun provideContactViewModelFactory(context: Context): ContactViewModelFactory {
        val contactRepo = ContactRepository.getInstance(context)
        return ContactViewModelFactory(contactRepo)
    }

    fun provideMessageViewModelFactory(context: Context): MessageViewModelFactory {
        val repository = getMessageRepository(context)
        return MessageViewModelFactory(repository)
    }

    private fun getMessageRepository(context: Context): MessageRepository {
        return MessageRepository.getInstance(AppDatabase.getInstance(context).messageDao())
    }

    fun provideConversationModelFactory(context: Context): ConversationViewModelFactory {
        val conversationRepo = ConversationRepository
                .getInstance(AppDatabase.getInstance(context).conversationDao())
        val contactRepo = ContactRepository.getInstance(context)
        val contactReadPermission = ContextCompat.checkSelfPermission(context,
                Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED

        return ConversationViewModelFactory(conversationRepo, contactRepo, contactReadPermission)
    }
}