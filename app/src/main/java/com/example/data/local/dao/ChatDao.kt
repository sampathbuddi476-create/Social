package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.Contact
import com.example.data.local.entity.Message
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for contacts and chat messages.
 */
@Dao
interface ChatDao {

    // --- Contact Queries & Inserts ---

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContact(contact: Contact): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContacts(contacts: List<Contact>): List<Long>

    @Update
    suspend fun updateContact(contact: Contact)

    @Delete
    suspend fun deleteContact(contact: Contact)

    @Query("SELECT * FROM contacts ORDER BY name ASC")
    fun getAllContacts(): Flow<List<Contact>>

    @Query("SELECT * FROM contacts WHERE id = :contactId")
    fun getContactById(contactId: Long): Flow<Contact?>

    @Query("SELECT * FROM contacts WHERE id = :contactId LIMIT 1")
    suspend fun getContactByIdSync(contactId: Long): Contact?

    // --- Message Queries & Inserts ---

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: Message): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<Message>): List<Long>

    @Delete
    suspend fun deleteMessage(message: Message)

    @Query("DELETE FROM messages WHERE contact_id = :contactId")
    suspend fun deleteMessagesForContact(contactId: Long)

    /**
     * Loads the chat history for a specific contact ordered chronologically (oldest to newest).
     */
    @Query("SELECT * FROM messages WHERE contact_id = :contactId ORDER BY timestamp ASC")
    fun getChatHistoryForContact(contactId: Long): Flow<List<Message>>

    /**
     * Synchronous query to load chat history for a specific contact.
     */
    @Query("SELECT * FROM messages WHERE contact_id = :contactId ORDER BY timestamp ASC")
    suspend fun getChatHistoryForContactSync(contactId: Long): List<Message>

    /**
     * Loads the latest message for a contact (useful for chat previews).
     */
    @Query("SELECT * FROM messages WHERE contact_id = :contactId ORDER BY timestamp DESC LIMIT 1")
    fun getLatestMessageForContact(contactId: Long): Flow<Message?>
}
