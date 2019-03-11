package com.sapp.yupi.data

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable

/**
 * Contact representation
 */
data class Contact(
        /**
         *  Dispaly name
         */
        var name: String,
        /**
         * Dispaly phone
         */
        var number: String,
        /**
         * Thumbnail photo uri
         */
        var thumbnailUri: Uri? = null
) : Parcelable {

    constructor(parcel: Parcel) : this(
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readParcelable(Uri::class.java.classLoader))

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(number)
        parcel.writeParcelable(thumbnailUri, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Contact> {
        override fun createFromParcel(parcel: Parcel): Contact {
            return Contact(parcel)
        }

        override fun newArray(size: Int): Array<Contact?> {
            return arrayOfNulls(size)
        }
    }
}
