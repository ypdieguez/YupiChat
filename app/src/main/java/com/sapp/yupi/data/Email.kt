package com.sapp.yupi.data

import androidx.annotation.NonNull
import androidx.room.Entity

@Entity(tableName = "emails")
class Email(@NonNull var address: String)