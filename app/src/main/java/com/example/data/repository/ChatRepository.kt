package com.example.data.repository

import com.example.data.local.dao.ChatDao
import com.example.data.local.entity.Contact
import com.example.data.local.entity.Message
import kotlinx.coroutines.flow.Flow

/**
 * Repository providing a clean abstraction over Room database data access
 * for contacts and chat messaging history.
 */
class ChatRepository(private val chatDao: ChatDao) {

    val allContacts: Flow<List<Contact>> = chatDao.getAllContacts()

    fun getContact(contactId: Long): Flow<Contact?> = chatDao.getContactById(contactId)

    suspend fun getContactSync(contactId: Long): Contact? = chatDao.getContactByIdSync(contactId)

    fun getChatHistory(contactId: Long): Flow<List<Message>> =
        chatDao.getChatHistoryForContact(contactId)

    suspend fun getChatHistorySync(contactId: Long): List<Message> =
        chatDao.getChatHistoryForContactSync(contactId)

    fun getLatestMessage(contactId: Long): Flow<Message?> =
        chatDao.getLatestMessageForContact(contactId)

    suspend fun insertContact(contact: Contact): Long = chatDao.insertContact(contact)

    suspend fun insertContacts(contacts: List<Contact>): List<Long> =
        chatDao.insertContacts(contacts)

    suspend fun insertMessage(message: Message): Long = chatDao.insertMessage(message)

    suspend fun insertMessages(messages: List<Message>): List<Long> =
        chatDao.insertMessages(messages)

    suspend fun deleteContact(contact: Contact) = chatDao.deleteContact(contact)

    suspend fun deleteMessage(message: Message) = chatDao.deleteMessage(message)

    suspend fun deleteMessagesForContact(contactId: Long) =
        chatDao.deleteMessagesForContact(contactId)
}
