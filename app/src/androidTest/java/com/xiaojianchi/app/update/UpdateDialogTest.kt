package com.xiaojianchi.app.update

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import org.junit.Test

class UpdateDialogTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun upToDateOnlyShowsPositiveConfirmation() {
        composeRule.setContent {
            UpdateDialog(
                state = UpdateState.UpToDate,
                onDismiss = {},
                onDownload = {},
                onInstall = { _, _ -> },
            )
        }

        composeRule.onNodeWithText("已经是最新版本").assertExists()
        composeRule.onNodeWithText("好的").assertExists()
        composeRule.onNodeWithText("暂不更新").assertDoesNotExist()
    }
}
