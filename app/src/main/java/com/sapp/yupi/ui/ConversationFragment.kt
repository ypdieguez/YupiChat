package com.sapp.yupi.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.sapp.yupi.Injector
import com.sapp.yupi.adapter.MessageAdapter
import com.sapp.yupi.data.Message
import com.sapp.yupi.databinding.FragmentConversationBinding
import com.sapp.yupi.viewmodel.MessageViewModel
import kotlinx.android.synthetic.main.view_compose_message_test.*

class ConversationFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding = FragmentConversationBinding.inflate(inflater, container, false)
        val context = context ?: return binding.root

        val factory = Injector.provideMessageViewModelFactory(context)
        val model = ViewModelProviders.of(this, factory).get(MessageViewModel::class.java)

        val contactId = ConversationFragmentArgs.fromBundle(arguments).contactId

        val adapter = MessageAdapter()
        binding.messageList.adapter = adapter

        model.getMessagesForContact(contactId).observe(this, Observer { messages ->

            adapter.mMaxWith = compose_message_text.width

            if (messages != null && messages.isNotEmpty()) {
                adapter.submitList(messages)
//                /binding.hasContacts = true
            } else {
                val messages2: List<Message> = listOf(
                        Message(1, "Hola Mundo", "9:30 am", 1),
                        Message(1, "Este es un mensaje mucho mas largo, es para probar portrait y landscape yo te voy a dar duro", "9:30 am", 0))
                adapter.submitList(messages2)
            }
        })

        setHasOptionsMenu(true)
        return binding.root
    }

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.fragment_conversation)
//        setSupportActionBar(toolbar)
//
//        val name = intent.getStringExtra("name")
//        val phone = intent.getStringExtra("phone")
//
////        val factory = Injector.provideContactViewModelFactory(this)
////        val model = ViewModelProviders.of(this, factory).get(ContactViewModel::class.java)
//
////        val liveContact = model.getMContact(id)
////
////        val name = liveContact.value?.name
////        val phone = liveContact.value?.phone
//
//        supportActionBar!!.apply {
//            title = name ?: phone
//            setDisplayHomeAsUpEnabled(true)
//        }
//
////        if(name != null && name.isNotEmpty()) {
////            supportActionBar!!.title = name
////        } else {
////            supportActionBar!!.title = phone
////        }
//
//        val pref = PreferenceManager.getDefaultSharedPreferences(this)
//        val nick = pref.getString(ConfigActivity.NICK, "")
//        val cell = pref.getString(ConfigActivity.CELL, "")
////        val user = pref.getString(ConfigActivity.EMAIL, "")
////        val pass = pref.getString(ConfigActivity.PASS, "")
////        if (nick.isEmpty() || cell.isEmpty() || user.isEmpty() || pass.isEmpty()) {
////            startActivity(Intent(this, ConfigActivity::class.java))
////            return
////        }
//
//        progressBar = progress
//        composeMessageTxt = composeMessage
//
//        sendMsgBtn2.setOnClickListener {
//            if (!composeMessage.text.isEmpty()) {
//                val msg = StringBuilder(composeMessage.text.toString()).appendln()
//                        .append("$nick $cell").toString()
//                SendEmailAsyncTask().execute(phone, msg)
//            } else {
//                Toast.makeText(this, "Dejaste un campo vacío", Toast.LENGTH_LONG).show()
//            }
//        }
//    }
//
//    override fun onBackPressed() {
//        moveTaskToBack(true)
//    }

//    private inner class SendEmailAsyncTask : AsyncTask<String, Void, String>() {
//        override fun onPreExecute() {
//            // Show the progress bar
//            progressBar.visibility = ProgressBar.VISIBLE
//        }
//
//        override fun doInBackground(vararg strings: String): String {
//            return MailSender.send(this@ConversationFragment, "gtom20180828@gmail.com",
//                    strings[0], strings[1])
//        }
//
//        override fun onPostExecute(result: String) {
//            Toast.makeText(this@ConversationFragment, result, Toast.LENGTH_LONG).show()
//            // Hide the progress bar
//            progressBar.visibility = ProgressBar.GONE
//            if (result == "OK") {
//                composeMessageTxt.text.clear()
//                Toast.makeText(this@ConversationFragment, "Mensaje enviado", Toast.LENGTH_LONG)
//                        .show()
//            }
//        }
//    }
}
