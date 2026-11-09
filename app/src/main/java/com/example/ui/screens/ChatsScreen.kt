package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.chat.ChatScreen
import com.example.ui.chat.ChatViewModel
import com.example.ui.chat.InboxScreen

/**
 * Top-level screen for the Chats tab. Coordinates between the [InboxScreen] (contact list)
 * and [ChatScreen] (conversation view).
 */
@Composable
fun ChatsScreen(
    modifier: Modifier = Modifier,
    viewModel: ChatViewModel = viewModel()
) {
    val contacts by viewModel.contacts.collectAsStateWithLifecycle()
    val selectedContact by viewModel.selectedContact.collectAsStateWithLifecycle()
    val currentMessages by viewModel.currentMessages.collectAsStateWithLifecycle()
    val messageInput by viewModel.messageInput.collectAsStateWithLifecycle()

    // Intercept back button to return from conversation view to Inbox
    BackHandler(enabled = selectedContact != null) {
        viewModel.selectContact(null)
    }

    AnimatedContent(
        targetState = selectedContact,
        transitionSpec = {
            if (targetState != null) {
                slideInHorizontally { width -> width } togetherWith slideOutHorizontally { width -> -width }
            } else {
                slideInHorizontally { width -> -width } togetherWith slideOutHorizontally { width -> width }
            }
        },
        label = "inbox_to_chat_transition",
        modifier = modifier.fillMaxSize()
    ) { contact ->
        if (contact == null) {
            InboxScreen(
                contacts = contacts,
                onContactClick = { chosenContact ->
                    viewModel.selectContact(chosenContact)
                },
                onAddContact = { name, persona, isBot ->
                    viewModel.addContact(name, persona, isBot)
                }
            )
        } else {
            ChatScreen(
                contact = contact,
                messages = currentMessages,
                inputText = messageInput,
                onInputChange = { text ->
                    viewModel.updateMessageInput(text)
                },
                onSendMessage = {
                    viewModel.sendMessage()
                },
                onBackClick = {
                    viewModel.selectContact(null)
                }
            )
        }
    }
}
