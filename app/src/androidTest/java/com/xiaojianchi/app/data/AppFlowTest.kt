package com.xiaojianchi.app.data

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.test.platform.app.InstrumentationRegistry
import com.xiaojianchi.app.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(androidx.compose.ui.test.ExperimentalTestApi::class)
class AppFlowTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun clearDatabase() =
        runBlocking {
            val context = InstrumentationRegistry.getInstrumentation().targetContext
            withContext(Dispatchers.IO) { AppDatabase.get(context).clearAllTables() }
            composeRule.waitForIdle()
        }

    @Test
    fun createCheckInAndWriteNote() {
        composeRule.onNodeWithText("写下第一个习惯").performClick()
        composeRule.onNodeWithText("例如：每天写一点").performTextInput("读书")
        composeRule.onNodeWithText("开始坚持").performClick()
        composeRule.onNodeWithText("读书").assertIsDisplayed()

        composeRule.onNodeWithContentDescription("完成读书今日打卡").performClick()
        composeRule.waitUntilExactlyOneExists(hasText("写一句"))
        composeRule.onNodeWithText("写一句").performClick()
        composeRule.onNodeWithText("今天只做了十分钟，但没有放弃。").performTextInput("今天只读了十分钟，也很好。")
        composeRule.onNodeWithText("留住今天").performClick()

        composeRule.onNodeWithText("足迹").performClick()
        composeRule.onNodeWithText("累计坚持").assertIsDisplayed()
        composeRule.onNodeWithText("当前连续").assertIsDisplayed()
        composeRule.onNodeWithText("“今天只读了十分钟，也很好。”").performScrollTo().assertIsDisplayed()
    }
}
