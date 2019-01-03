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
const val CONTACT_ID = "contact_id"
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
const val PREF_FIRST_TIME_ASKED_READ_SMS_PERMISSION = "first_time_asked_read_sms_permission"