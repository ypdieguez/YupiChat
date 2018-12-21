package com.sapp.yupi

import android.content.Context
import android.content.SharedPreferences
import com.sapp.yupi.data.AppDatabase
import com.sapp.yupi.ui.appintro.PREF_EMAIL
import com.sapp.yupi.ui.appintro.PREF_EMAIL_PASS
import com.sapp.yupi.ui.appintro.USER_PREFERENCES
import com.sun.mail.util.MailConnectException
import javax.mail.AuthenticationFailedException
import javax.mail.Message
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

/**
 * Email status
 */
const val STATUS_MAIL_CONNECT_EXCEPTION: Byte = 3
const val STATUS_AUTHENTICATION_FAILED_EXCEPTION: Byte = 4
const val STATUS_OHTER_EXCEPTION: Byte = 5

/**
 * Must be init in App.
 */
class Email {
    companion object {

        private lateinit var pref: SharedPreferences

        fun init(context: Context) {
            pref = context.getSharedPreferences(USER_PREFERENCES, Context.MODE_PRIVATE)
        }

        fun send(address: String, subject: String, content: String): Byte {
            try {
                val user = pref.getString(PREF_EMAIL, "")
                val pass = pref.getString(PREF_EMAIL_PASS, "")

                val props = System.getProperties()
                props["mail.smtp.host"] = "smtp.nauta.cu"
                props["mail.smtp.port"] = "25"

                val session = Session.getInstance(props)

                val msg = MimeMessage(session)
                msg.setFrom(InternetAddress(user))
                msg.setRecipients(Message.RecipientType.TO,
                        InternetAddress.parse(address, false))
                msg.subject = subject
                msg.setText(content)

                Transport.send(msg, user, pass)

//                return "OK"
                return STATUS_SUCCESS
            } catch (e: MailConnectException) {
//                return "No se pudo conectar al host, puede ser por no tener activado el acceso a datos."
                return STATUS_MAIL_CONNECT_EXCEPTION
            } catch (e: AuthenticationFailedException) {
                return STATUS_AUTHENTICATION_FAILED_EXCEPTION
//                return "Usuario o contraseña incorrecto."
            }
        }
    }
}