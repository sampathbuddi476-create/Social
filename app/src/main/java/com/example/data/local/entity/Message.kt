package com.example.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Data entity representing a chat message.
 *
 * @property id Unique identifier for the message. Auto-generated when 0.
 * @property contactId The ID of the contact this message belongs to.
 * @property text Content of the message.
 * @property timestamp Epoch time in milliseconds when the message was sent/received.
 * @property isFromUser True if the message was authored by the user; false if from the contact/bot.
 */
@Entity(
    tableName = "messages",
    foreignKeys = [
        ForeignKey(
            entity = Contact::class,
            parentColumns = ["id"],
            childColumns = ["contact_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["contact_id"]),
        Index(value = ["timestamp"])
    ]
)
data class Message(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "contact_id")
    val contactId: Long,

    val text: String,

    val timestamp: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "is_from_user")
    val isFromUser: Boolean = true
)
