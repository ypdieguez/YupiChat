package com.github.sapp.yupi.ui

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.appcompat.app.AppCompatActivity
import android.widget.ProgressBar
import android.widget.Toast
import com.github.sapp.yupi.MailSender
import com.github.sapp.yupi.R
import kotlinx.android.synthetic.main.view_intro.*


class ConfigActivity : AppCompatActivity() {
    companion object {
        const val NICK = "nick"
        const val CELL = "cell"
        const val EMAIL = "email"
        const val PASS = "pass"
    }

    lateinit var progressBar: ProgressBar
    lateinit var mNick: String
    lateinit var mCell: String
    lateinit var mEmail: String
    lateinit var mPass: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_intro)

        progressBar = progress

        okBtn.setOnClickListener {
            mNick = nick.text.toString()
            mCell = cell.text.toString()
            mEmail = email.text.toString()
            mPass = pass.text.toString()
            if (!mNick.isEmpty() && !mCell.isEmpty() && !mEmail.isEmpty() && !mPass.isEmpty()) {
                val pref = PreferenceManager.getDefaultSharedPreferences(this)
                pref.edit().putString(EMAIL, mEmail).apply()
                pref.edit().putString(PASS, mPass).apply()
                // Check
                SendEmailAsyncTask().execute()
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
            return MailSender.send(this@ConfigActivity, "2Dzf4fCdJqMiAfZr@gmail.com",
                    "Yuuupi Telegram", "Suscripcion")
        }

        override fun onPostExecute(result: String) {
            Toast.makeText(this@ConfigActivity, result, Toast.LENGTH_LONG).show()
            // Hide the progress bar
            progressBar.visibility = ProgressBar.GONE

            if (result == "OK") {
                val pref = PreferenceManager.getDefaultSharedPreferences(this@ConfigActivity)
                pref.edit().putString(NICK, mNick).apply()
                pref.edit().putString(CELL, mCell).apply()

                startActivity(Intent(this@ConfigActivity, MainActivity::class.java))
            }
        }
    }
}