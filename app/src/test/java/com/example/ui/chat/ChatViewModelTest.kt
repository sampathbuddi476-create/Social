package com.example.ui.chat

import android.app.Application
import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AppDatabase
import com.example.data.local.entity.Contact
import com.example.data.repository.ChatRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ChatViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var database: AppDatabase
    private lateinit var repository: ChatRepository
    private lateinit var application: Application
    private lateinit var viewModel: ChatViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        val context = ApplicationProvider.getApplicationContext<Context>()
        application = ApplicationProvider.getApplicationContext()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = ChatRepository(database.chatDao())
        viewModel = ChatViewModel(application = application, repository = repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        database.close()
    }

    @Test
    fun testSelectContactAndSendMessage() = runTest {
        // Create and insert a contact
        val contactId = repository.insertContact(
            Contact(name = "Test Bot", personaInstructions = "Helpful bot", isGuideBot = true)
        )
        val contact = repository.getContactSync(contactId)
        assertNotNull(contact)

        // Select contact
        viewModel.selectContact(contact)
        assertEquals(contact, viewModel.selectedContact.value)

        // Enter text and send
        viewModel.updateMessageInput("Hello Test Bot!")
        assertEquals("Hello Test Bot!", viewModel.messageInput.value)

        viewModel.sendMessage()
        // Message input cleared immediately
        assertEquals("", viewModel.messageInput.value)

        // Advance coroutines
        advanceUntilIdle()

        // Verify message was stored in database
        val history = repository.getChatHistorySync(contactId)
        assertTrue(history.isNotEmpty())
        val userMsg = history.find { it.text == "Hello Test Bot!" }
        assertNotNull(userMsg)
        assertTrue(userMsg?.isFromUser == true)
    }

    @Test
    fun testAddContact() = runTest {
        viewModel.addContact("New Contact", "Sample persona", false)
        advanceUntilIdle()

        val contactsList = repository.allContacts.first()
        val created = contactsList.find { it.name == "New Contact" }
        assertNotNull(created)
        assertEquals("Sample persona", created?.personaInstructions)
    }
}
