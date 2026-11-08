package com.example.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.R

/**
 * Navigation tabs for the messaging application bottom bar.
 */
enum class NavigationTab(
    @StringRes val titleRes: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String,
    val screenTag: String
) {
    CHATS(
        titleRes = R.string.tab_chats,
        selectedIcon = Icons.Filled.ChatBubble,
        unselectedIcon = Icons.Outlined.ChatBubbleOutline,
        testTag = "nav_item_chats",
        screenTag = "screen_chats"
    ),
    STORY(
        titleRes = R.string.tab_story,
        selectedIcon = Icons.Filled.AutoStories,
        unselectedIcon = Icons.Outlined.AutoStories,
        testTag = "nav_item_story",
        screenTag = "screen_story"
    ),
    CALLS(
        titleRes = R.string.tab_calls,
        selectedIcon = Icons.Filled.Call,
        unselectedIcon = Icons.Outlined.Call,
        testTag = "nav_item_calls",
        screenTag = "screen_calls"
    );

    companion object {
        val DEFAULT = CHATS
    }
}
