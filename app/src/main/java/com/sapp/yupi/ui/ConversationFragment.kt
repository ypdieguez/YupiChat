package com.sapp.yupi.ui

import android.os.Bundle
import android.text.method.TextKeyListener
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.sapp.yupi.CONTACT_ID
import com.sapp.yupi.Injector
import com.sapp.yupi.MESSAGE
import com.sapp.yupi.adapters.MessageAdapter
import com.sapp.yupi.databinding.FragmentConversationBinding
import com.sapp.yupi.viewmodel.MessageViewModel
import com.sapp.yupi.workers.OutgoingMsgWorker


class ConversationFragment : Fragment() {

    private lateinit var model: MessageViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding = FragmentConversationBinding.inflate(inflater, container, false)
        val context = context ?: return binding.root

        val factory = Injector.provideMessageViewModelFactory(context)
        model = ViewModelProviders.of(this, factory).get(MessageViewModel::class.java)

        val contactId = ConversationFragmentArgs.fromBundle(arguments!!).contactId.toLong()

        val adapter = MessageAdapter()
        binding.apply {
            messageList.let {

                val manager = LinearLayoutManager(activity)
                manager.stackFromEnd = true

                adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
                    override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                        manager.smoothScrollToPosition(it, null,
                                positionStart + itemCount)
                    }
                })

                it.adapter = adapter
                it.layoutManager = manager
            }

            composeMessage.apply {
                textEditor.requestFocus()

                sendMessageBtn.setOnClickListener {
                    val msg = textEditor.text.toString()
                    if (msg.isNotEmpty()) {
                        // Send msg
                        sendMsg(contactId, msg)
                        // Focus to the text editor.
                        textEditor.requestFocus()
                        // Clear the text box.
                        TextKeyListener.clear(textEditor.text)
                    }
                }
            }
        }

        model.getMessagesForContact(contactId).observe(this, Observer { messages ->
//            adapter.mMaxWith = compose_message_text.width
            if (messages != null && messages.isNotEmpty()) {
                adapter.submitList(messages)
            }
        })

        setHasOptionsMenu(true)
        return binding.root
    }

    private fun sendMsg(contactId: Long, txt: String) {
        val data = Data.Builder()
                .putLong(CONTACT_ID, contactId)
                .putString(MESSAGE, txt)
                .build()

        val sendMsgWorker = OneTimeWorkRequest.Builder(OutgoingMsgWorker::class.java)
                .setInputData(data)
                .build()

        val workManager = WorkManager.getInstance()
        workManager.enqueue(sendMsgWorker)
        workManager.getWorkInfoByIdLiveData(sendMsgWorker.id)
                .observe(this, Observer { info ->
                    if (info != null && info.state.isFinished) {
                        info.outputData
                    }
                })
    }

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
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
