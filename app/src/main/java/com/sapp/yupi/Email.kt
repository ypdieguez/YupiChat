package com.sapp.yupi

import android.content.Context
import android.content.SharedPreferences
import com.sapp.yupi.util.UserPrefUtil
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

class Email {
    companion object {
        fun send(address: String, subject: String, content: String): Byte {
            try {
                val user = UserPrefUtil.getEmail()
                val pass = UserPrefUtil.getEmailPass()

                val props = System.getProperties()
                props["mail.smtp.host"] = BuildConfig.HOST
                props["mail.smtp.port"] = BuildConfig.PORT
                props["mail.smtp.ssl.enable"] = BuildConfig.SSL_ENABLED

                val session = Session.getInstance(props)

                val msg = MimeMessage(session)
                msg.setFrom(InternetAddress(user))
                msg.setRecipients(Message.RecipientType.TO,
                        InternetAddress.parse(address, false))
                msg.subject = subject
                msg.setText(content)

                Transport.send(msg, user, pass)

                return STATUS_SUCCESS
            } catch (e: MailConnectException) {
                return STATUS_MAIL_CONNECT_EXCEPTION
            } catch (e: AuthenticationFailedException) {
                return STATUS_AUTHENTICATION_FAILED_EXCEPTION
            }
        }
    }
}