package com.example.data.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.dao.ChatDao
import com.example.data.local.entity.Contact
import com.example.data.local.entity.Message
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ChatDatabaseTest {

    private lateinit var database: AppDatabase
    private lateinit var chatDao: ChatDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        chatDao = database.chatDao()
    }

    @After
    fun closeDb() {
        database.close()
    }

    @Test
    fun insertAndQueryContact() = runBlocking {
        val contact = Contact(
            name = "Support Bot",
            personaInstructions = "You are a helpful customer support bot.",
            isGuideBot = true
        )
        val contactId = chatDao.insertContact(contact)
        assertTrue(contactId > 0)

        val retrievedContact = chatDao.getContactByIdSync(contactId)
        assertNotNull(retrievedContact)
        assertEquals("Support Bot", retrievedContact?.name)
        assertEquals("You are a helpful customer support bot.", retrievedContact?.personaInstructions)
        assertTrue(retrievedContact?.isGuideBot == true)
    }

    @Test
    fun insertAndQueryChatHistoryForSpecificContact() = runBlocking {
        // Create 2 contacts
        val aliceId = chatDao.insertContact(
            Contact(name = "Alice", personaInstructions = "Friendly colleague", isGuideBot = false)
        )
        val bobId = chatDao.insertContact(
            Contact(name = "Bob", personaInstructions = "Project manager", isGuideBot = false)
        )

        // Insert messages for Alice
        val msg1 = Message(
            contactId = aliceId,
            text = "Hi Alice!",
            timestamp = 1000L,
            isFromUser = true
        )
        val msg2 = Message(
            contactId = aliceId,
            text = "Hello! How are you?",
            timestamp = 2000L,
            isFromUser = false
        )
        // Insert message for Bob
        val msgBob = Message(
            contactId = bobId,
            text = "Hey Bob, check the report.",
            timestamp = 1500L,
            isFromUser = true
        )

        chatDao.insertMessages(listOf(msg1, msg2, msgBob))

        // Query chat history for Alice
        val aliceHistory = chatDao.getChatHistoryForContact(aliceId).first()
        assertEquals(2, aliceHistory.size)
        assertEquals("Hi Alice!", aliceHistory[0].text)
        assertTrue(aliceHistory[0].isFromUser)
        assertEquals("Hello! How are you?", aliceHistory[1].text)
        assertFalse(aliceHistory[1].isFromUser)

        // Query chat history for Bob
        val bobHistory = chatDao.getChatHistoryForContactSync(bobId)
        assertEquals(1, bobHistory.size)
        assertEquals("Hey Bob, check the report.", bobHistory[0].text)
    }
}
