package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.ui.navigation.NavigationTab
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Messenger", appName)
  }

  @Test
  fun `verify exactly three navigation tabs and default launch tab`() {
    val tabs = NavigationTab.entries
    assertEquals(3, tabs.size)
    assertEquals(NavigationTab.CHATS, NavigationTab.DEFAULT)
    assertTrue(tabs.contains(NavigationTab.CHATS))
    assertTrue(tabs.contains(NavigationTab.STORY))
    assertTrue(tabs.contains(NavigationTab.CALLS))
  }

  @Test
  fun `verify tab string resources`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    assertEquals("Chats", context.getString(NavigationTab.CHATS.titleRes))
    assertEquals("Story", context.getString(NavigationTab.STORY.titleRes))
    assertEquals("Calls", context.getString(NavigationTab.CALLS.titleRes))
  }
}
