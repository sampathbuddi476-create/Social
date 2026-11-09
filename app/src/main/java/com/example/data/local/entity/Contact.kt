package com.example.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Data entity representing a chat contact or bot in the messaging application.
 *
 * @property id Unique identifier for the contact. Auto-generated when 0.
 * @property name Display name of the contact.
 * @property personaInstructions Custom instructions or persona description for the contact/bot.
 * @property isGuideBot Whether this contact is an automated guide/assistant bot.
 */
@Entity(tableName = "contacts")
data class Contact(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val name: String,

    @ColumnInfo(name = "persona_instructions")
    val personaInstructions: String = "",

    @ColumnInfo(name = "is_guide_bot")
    val isGuideBot: Boolean = false
)
