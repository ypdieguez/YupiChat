package com.sapp.yupi

import android.content.Context
import android.preference.PreferenceManager
import com.sapp.yupi.ui.ConfigActivity
import com.sun.mail.util.MailConnectException
import javax.mail.AuthenticationFailedException
import javax.mail.Message
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

class MailSender {
    companion object {
        fun send(context: Context, address: String, subject: String, content: String): String {
            try {
                val pref = PreferenceManager.getDefaultSharedPreferences(context)
                val user = pref.getString(ConfigActivity.EMAIL, "")
                val pass = pref.getString(ConfigActivity.PASS, "")

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

                return "OK"
            } catch (e: MailConnectException) {
                return "No se pudo conectar al host, puede ser por no tener activado el acceso a datos."
            } catch (e: AuthenticationFailedException) {
                return "Usuario o contraseña incorrecto."
            } catch (e: Exception) {
                return e.message.toString()
            }
        }
    }
}