package com.sapp.yupi.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "emails")
class Email(@PrimaryKey var address: String)