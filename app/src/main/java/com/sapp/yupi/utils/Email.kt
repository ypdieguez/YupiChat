package com.sapp.yupi.utils

import android.content.Context
import com.sapp.yupi.Config
import com.sapp.yupi.STATUS_SUCCESS
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
const val STATUS_OTHER_EXCEPTION: Byte = 5

class Email private constructor(context: Context) {

    private val config = Config.getInstance(context)

    /**
     * Send message via email.
     *
     * @param address Email address who will process the message.
     * @param subject Phone that will receive the message.
     * @param content Message to handleOutgoingMsg.
     */
    fun send(address: String, subject: String, content: String): Byte {
        try {
            val props = System.getProperties()
            props["mail.smtp.host"] = config.host
            props["mail.smtp.port"] = config.port
            props["mail.smtp.ssl.enable"] = config.sslEnabled

            val session = Session.getInstance(props)

            val msg = MimeMessage(session)
            msg.setFrom(InternetAddress(config.email))
            msg.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(address, false))
            msg.subject = subject
            msg.setText(SmsUtil.createMsg(content, config.phone))

            Transport.send(msg, config.email, config.pass)

            return STATUS_SUCCESS
        } catch (e: MailConnectException) {
            return STATUS_MAIL_CONNECT_EXCEPTION
        } catch (e: AuthenticationFailedException) {
            return STATUS_AUTHENTICATION_FAILED_EXCEPTION
        }
    }

    companion object {
            @Volatile
            private var instance: Email? = null

            fun getInstance(context: Context): Email {
                return instance ?: synchronized(this) {
                    instance ?: init(context).also { instance = it }
                }
            }

            private fun init(context: Context) = Email(context)
    }
}