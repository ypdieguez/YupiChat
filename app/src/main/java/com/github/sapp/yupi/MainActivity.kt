package com.github.sapp.yupi

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_conversation.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit var progressBar: ProgressBar
    lateinit var composeMessageTxt: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conversation)
        setSupportActionBar(toolbar)

        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        val nick = pref.getString(IntroActivity.NICK, "")
        val cell = pref.getString(IntroActivity.CELL, "")
        val user = pref.getString(IntroActivity.EMAIL, "")
        val pass = pref.getString(IntroActivity.PASS, "")
        if (nick.isEmpty() || cell.isEmpty() || user.isEmpty() || pass.isEmpty()) {
            startActivity(Intent(this, IntroActivity::class.java))
            return
        }

        progressBar = progress
        composeMessageTxt = composeMessage

        val adapter = ArrayAdapter.createFromResource(this, R.array.countries_code,
                android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        codeCountrySpinner.adapter = adapter

        sendMsgBtn2.setOnClickListener {
            if (!numberEditText.text.isEmpty() && !composeMessage.text.isEmpty()) {
                val phone: String = if (codeCountrySpinner.selectedItemPosition == 0
                        || codeCountrySpinner.selectedItemPosition == 1) {
                    StringBuilder("+1").append(numberEditText.text.toString()).toString()
                } else {
                    StringBuilder("+52").append(numberEditText.text.toString()).toString()
                }
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
            return MailSender.send(this@MainActivity, "gtom20180828@gmail.com",
                    strings[0], strings[1])
        }

        override fun onPostExecute(result: String) {
            Toast.makeText(this@MainActivity, result, Toast.LENGTH_LONG).show()
            // Hide the progress bar
            progressBar.visibility = ProgressBar.GONE
            if (result == "OK") {
                composeMessageTxt.text.clear()
                Toast.makeText(this@MainActivity, "Mensaje enviado", Toast.LENGTH_LONG)
                        .show()
            }
        }
    }
}
