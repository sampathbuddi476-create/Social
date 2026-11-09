package com.example.ui.chat

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.entity.Contact
import com.example.data.local.entity.Message
import com.example.data.repository.ChatRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel responsible for managing contacts, chat history, and messaging actions.
 * Connects directly to Room Database via [ChatRepository].
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ChatViewModel(
    application: Application,
    private val repository: ChatRepository = ChatRepository(
        AppDatabase.getDatabase(application).chatDao()
    )
) : AndroidViewModel(application) {

    // Reactive list of all contacts from Room
    val contacts: StateFlow<List<Contact>> = repository.allContacts
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Currently active contact for chat view
    private val _selectedContact = MutableStateFlow<Contact?>(null)
    val selectedContact: StateFlow<Contact?> = _selectedContact.asStateFlow()

    // Current message input text
    private val _messageInput = MutableStateFlow("")
    val messageInput: StateFlow<String> = _messageInput.asStateFlow()

    // Automatically load chat history whenever selectedContact changes
    val currentMessages: StateFlow<List<Message>> = _selectedContact
        .flatMapLatest { contact ->
            if (contact != null) {
                repository.getChatHistory(contact.id)
            } else {
                flowOf(emptyList())
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        // Pre-populate with sample contacts and starter messages if empty
        seedInitialDataIfEmpty()
    }

    fun selectContact(contact: Contact?) {
        _selectedContact.value = contact
        _messageInput.value = ""
    }

    fun updateMessageInput(newText: String) {
        _messageInput.value = newText
    }

    fun sendMessage() {
        val contact = _selectedContact.value ?: return
        val text = _messageInput.value.trim()
        if (text.isEmpty()) return

        // Clear input immediately for responsive feel
        _messageInput.value = ""

        viewModelScope.launch {
            // Insert user message into Room
            val userMessage = Message(
                contactId = contact.id,
                text = text,
                timestamp = System.currentTimeMillis(),
                isFromUser = true
            )
            repository.insertMessage(userMessage)

            // If the contact is an AI guide bot, simulate a contextual bot reply
            if (contact.isGuideBot) {
                simulateAiResponse(contact, text)
            }
        }
    }

    private fun simulateAiResponse(contact: Contact, userPrompt: String) {
        viewModelScope.launch {
            delay(750) // Realistic typing delay
            val replyText = generateBotReply(contact, userPrompt)
            val aiMessage = Message(
                contactId = contact.id,
                text = replyText,
                timestamp = System.currentTimeMillis(),
                isFromUser = false
            )
            repository.insertMessage(aiMessage)
        }
    }

    private fun generateBotReply(contact: Contact, prompt: String): String {
        val lower = prompt.lowercase()
        return when {
            lower.contains("hello") || lower.contains("hi") || lower.contains("hey") ->
                "Hello! I am ${contact.name}. How can I assist you with your chats today?"
            lower.contains("help") || lower.contains("guide") ->
                "I'm here to help! You can browse your inbox, send and receive messages, or customize contact personas."
            lower.contains("who are you") ->
                if (contact.personaInstructions.isNotBlank()) contact.personaInstructions
                else "I am ${contact.name}, your AI companion."
            lower.contains("how are you") ->
                "I'm running smoothly and ready to assist you!"
            else ->
                "I received your message: \"$prompt\". As ${contact.name}, I'm always available to assist you in real time."
        }
    }

    fun addContact(name: String, personaInstructions: String, isGuideBot: Boolean) {
        if (name.isBlank()) return
        viewModelScope.launch {
            val newContact = Contact(
                name = name.trim(),
                personaInstructions = personaInstructions.trim(),
                isGuideBot = isGuideBot
            )
            val contactId = repository.insertContact(newContact)

            // Add an introductory greeting
            val greeting = if (isGuideBot) {
                "Hello! I am $name. Feel free to ask me anything!"
            } else {
                "Hey there! Glad to connect."
            }
            repository.insertMessage(
                Message(
                    contactId = contactId,
                    text = greeting,
                    timestamp = System.currentTimeMillis(),
                    isFromUser = false
                )
            )
        }
    }

    private fun seedInitialDataIfEmpty() {
        viewModelScope.launch {
            // Check if database already has contacts
            val currentList = repository.getContactSync(1L)
            if (currentList == null) {
                // Seed AI Guide Bot
                val guideBotId = repository.insertContact(
                    Contact(
                        name = "AI Guide Bot",
                        personaInstructions = "I am the intelligent guide assistant for your Messenger app.",
                        isGuideBot = true
                    )
                )
                repository.insertMessage(
                    Message(
                        contactId = guideBotId,
                        text = "Welcome to Messenger! I am your AI Guide Bot. Feel free to send me a message anytime.",
                        timestamp = System.currentTimeMillis() - 60000,
                        isFromUser = false
                    )
                )

                // Seed regular contacts
                val contact1Id = repository.insertContact(
                    Contact(
                        name = "Sarah Miller",
                        personaInstructions = "Mobile app designer and developer",
                        isGuideBot = false
                    )
                )
                repository.insertMessage(
                    Message(
                        contactId = contact1Id,
                        text = "Hey! Did you check out the new design mockups?",
                        timestamp = System.currentTimeMillis() - 3600000,
                        isFromUser = false
                    )
                )

                val contact2Id = repository.insertContact(
                    Contact(
                        name = "David Chen",
                        personaInstructions = "Backend engineer",
                        isGuideBot = false
                    )
                )
                repository.insertMessage(
                    Message(
                        contactId = contact2Id,
                        text = "Database tables are set up and synced.",
                        timestamp = System.currentTimeMillis() - 86400000,
                        isFromUser = false
                    )
                )
            }
        }
    }
}
