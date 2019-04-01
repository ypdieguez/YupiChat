package com.sapp.yupi

/**
 * Constants used throughout the app.
 */

/**
 * DB name
 */
const val DATABASE_NAME = "yuuupi_database"

/**
 * DB message type
 */
const val TYPE_INCOMING = true
const val TYPE_OUTGOING = false

/**
 * OutgoingMsgWorker data
 */
const val CONTACT_PHONE = "phone"
const val MESSAGE = "message"

/**
 * Email status
 */
const val STATUS_SENDING: Byte = 1
const val STATUS_SUCCESS: Byte = 2

/**
 * Preferences permissions
 */
const val PERMISSION_PREFERENCES = "permissions_preferences"
const val PREF_READ_SMS_PERMISSION_ASKED = "first_time_asked_read_sms_permission"
const val PREF_READ_CONTACT_PERMISSION_ASKED = "first_time_asked_read_contact_permission"
const val PREF_READ_CONTACT_PERMISSION_ASKED_FROM_RESUME = "asked_read_contact_permission_from_resume"

/**
 * Notification
 */
const val BROADCAST_NOTIFICATION = "${BuildConfig.APPLICATION_ID}.BROADCAST_NOTIFICATION"
const val NOTIFICATION = "${BuildConfig.APPLICATION_ID}.NOTIFICATION"
const val PHONE_NOTIFICATION = "${BuildConfig.APPLICATION_ID}.PHONE_NOTIFICATION"