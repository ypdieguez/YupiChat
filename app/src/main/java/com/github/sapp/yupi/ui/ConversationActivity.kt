package com.github.sapp.yupi.ui

import android.os.AsyncTask
import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.github.sapp.yupi.Injector
import com.github.sapp.yupi.MailSender
import com.github.sapp.yupi.R
import com.github.sapp.yupi.viewmodel.ContactViewModel
import kotlinx.android.synthetic.main.activity_conversation.*
import kotlinx.android.synthetic.main.toolbar.*

class ConversationActivity : AppCompatActivity() {

    lateinit var progressBar: ProgressBar
    lateinit var composeMessageTxt: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conversation)
        setSupportActionBar(toolbar)

        val name = intent.getStringExtra("name")
        val phone = intent.getStringExtra("phone")

//        val factory = Injector.provideContactViewModelFactory(this)
//        val model = ViewModelProviders.of(this, factory).get(ContactViewModel::class.java)

//        val liveContact = model.getMContact(id)
//
//        val name = liveContact.value?.name
//        val phone = liveContact.value?.phone

        supportActionBar!!.apply {
            title = name ?: phone
            setDisplayHomeAsUpEnabled(true)
        }

//        if(name != null && name.isNotEmpty()) {
//            supportActionBar!!.title = name
//        } else {
//            supportActionBar!!.title = phone
//        }

        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        val nick = pref.getString(ConfigActivity.NICK, "")
        val cell = pref.getString(ConfigActivity.CELL, "")
//        val user = pref.getString(ConfigActivity.EMAIL, "")
//        val pass = pref.getString(ConfigActivity.PASS, "")
//        if (nick.isEmpty() || cell.isEmpty() || user.isEmpty() || pass.isEmpty()) {
//            startActivity(Intent(this, ConfigActivity::class.java))
//            return
//        }

        progressBar = progress
        composeMessageTxt = composeMessage

        sendMsgBtn2.setOnClickListener {
            if (!composeMessage.text.isEmpty()) {
                val msg = StringBuilder(composeMessage.text.toString()).appendln()
                        .append("$nick $cell").toString()
                SendEmailAsyncTask().execute(phone, msg)
            } else {
                Toast.makeText(this, "Dejaste un campo vacío", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onBackPressed() {
        moveTaskToBack(true)
    }

    private inner class SendEmailAsyncTask : AsyncTask<String, Void, String>() {
        override fun onPreExecute() {
            // Show the progress bar
            progressBar.visibility = ProgressBar.VISIBLE
        }

        override fun doInBackground(vararg strings: String): String {
            return MailSender.send(this@ConversationActivity, "gtom20180828@gmail.com",
                    strings[0], strings[1])
        }

        override fun onPostExecute(result: String) {
            Toast.makeText(this@ConversationActivity, result, Toast.LENGTH_LONG).show()
            // Hide the progress bar
            progressBar.visibility = ProgressBar.GONE
            if (result == "OK") {
                composeMessageTxt.text.clear()
                Toast.makeText(this@ConversationActivity, "Mensaje enviado", Toast.LENGTH_LONG)
                        .show()
            }
        }
    }
}
