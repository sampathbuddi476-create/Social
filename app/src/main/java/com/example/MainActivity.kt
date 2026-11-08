package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.ui.components.MessengerBottomBar
import com.example.ui.navigation.NavigationTab
import com.example.ui.screens.CallsScreen
import com.example.ui.screens.ChatsScreen
import com.example.ui.screens.StoryScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MessengerApp()
            }
        }
    }
}

@Composable
fun MessengerApp(
    modifier: Modifier = Modifier
) {
    // Make 'Chats' the default launch screen
    var currentTab by rememberSaveable { mutableStateOf(NavigationTab.DEFAULT) }

    // Intercept back presses to return to Chats if on another tab
    BackHandler(enabled = currentTab != NavigationTab.CHATS) {
        currentTab = NavigationTab.CHATS
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            MessengerBottomBar(
                currentTab = currentTab,
                onTabSelected = { selectedTab ->
                    currentTab = selectedTab
                }
            )
        }
    ) { innerPadding ->
        AnimatedContent(
            targetState = currentTab,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "tab_transition",
            modifier = Modifier.padding(innerPadding)
        ) { tab ->
            when (tab) {
                NavigationTab.CHATS -> ChatsScreen()
                NavigationTab.STORY -> StoryScreen()
                NavigationTab.CALLS -> CallsScreen()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MessengerAppPreview() {
    MyApplicationTheme {
        MessengerApp()
    }
}
