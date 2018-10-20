package com.sapp.yupi.data

import androidx.annotation.NonNull
import androidx.room.*
import androidx.room.ForeignKey.CASCADE

@Entity(
        tableName = "messages",
        foreignKeys = [ForeignKey(entity = Contact::class, parentColumns = ["id"],
                childColumns = ["contact_id"], onDelete = CASCADE, onUpdate = CASCADE)],
        indices = [Index("text")]
)
class Message(
        @NonNull
        @ColumnInfo(name = "contact_id")
        var contactId: Int,
        @NonNull
        var text: String,
        @NonNull
        var date: String,
        @NonNull
        var type: Int
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}