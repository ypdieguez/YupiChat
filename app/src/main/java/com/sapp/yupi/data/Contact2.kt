package com.sapp.yupi.data

import android.net.Uri

/**
 * Contact representation
 */
class Contact2 {
    var id: Long = 0L // CONTACT_ID
    var name: String = String() // display_name
    var number: String = String()
    var photoUri: Uri? = null
    var thumbnailUri: Uri? = null
}