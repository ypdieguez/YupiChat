package com.github.sapp.yupi

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_conversation.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.view_compose_message.*
import java.util.*
import javax.mail.Message
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conversation)
        setSupportActionBar(toolbar)

        val adapter = ArrayAdapter.createFromResource(this, R.array.countries_code,
                android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        codeCountrySpinner.adapter = adapter

        sendMsgBtn.setOnClickListener {
            if(!numberEditText.text.isEmpty() && !composeMessageText.text.isEmpty()) {
                Thread(Runnable { try {
                    val phone = StringBuilder(codeCountrySpinner.selectedItem.toString())
                            .append(numberEditText).toString()
//                    Toast.makeText(this, phone, Toast.LENGTH_LONG).show()

                    val props = Properties()
                    props["mail.smtp.host"] = "smtp.gmail.com"
                    props.setProperty("mail.smtp.ssl.enable", "true")

                    val session = Session.getInstance(props)

                    val msg = MimeMessage(session)
                    msg.setFrom()
                    msg.setRecipients(Message.RecipientType.TO,
                            InternetAddress.parse("ypdieguez@gmail.com", false))
                    msg.subject = StringBuilder(codeCountrySpinner.selectedItem.toString())
                            .append(numberEditText).toString()
                    msg.setText(composeMessageText.text.toString())
                    msg.setHeader("X-Mailer", "Yuuupi")
                    msg.sentDate = Date()

                    Transport.send(msg, "gtom20180828@gmail.com", "GtoM_2018/08/28")
                } catch (e: Exception) {
                    val a = 1
                }
                }).start()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
