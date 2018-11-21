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
 * Mail status
 */
const val STATUS_SENDING: Byte = 1
const val STATUS_SUCCESS: Byte = 2
const val STATUS_MAIL_CONNECT_EXCEPTION: Byte = 3
const val STATUS_AUTHENTICATION_FAILED_EXCEPTION: Byte = 4
const val STATUS_OHTER_EXCEPTION: Byte = 5