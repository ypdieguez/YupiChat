package com.sapp.yupi.data

import android.os.Parcel
import android.os.Parcelable

open class Notification(
        val contact: Contact,
        val message: String
) : Parcelable {

    constructor(parcel: Parcel) : this(
            parcel.readParcelable(Contact::class.java.classLoader)!!,
            parcel.readString()!!)

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(contact, flags)
        parcel.writeString(message)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Notification> {
        override fun createFromParcel(parcel: Parcel): Notification {
            return Notification(parcel)
        }

        override fun newArray(size: Int): Array<Notification?> {
            return arrayOfNulls(size)
        }
    }
}