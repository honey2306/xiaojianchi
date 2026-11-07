package com.xiaojianchi.app.update

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xiaojianchi.app.ui.PaperLight

@Composable
fun UpdateDialog(
    state: UpdateState,
    onDismiss: () -> Unit,
    onDownload: (UpdateManifest) -> Unit,
    onInstall: (UpdateManifest, java.io.File) -> Unit,
) {
    if (state == UpdateState.Idle || state == UpdateState.Checking) return
    when (state) {
        is UpdateState.Available -> {
            UpdateAlert(
                title = "发现新版本 ${state.manifest.versionName}",
                onDismiss = onDismiss,
                confirm = "下载更新",
                onConfirm = { onDownload(state.manifest) },
            ) {
                Text(state.manifest.notes, lineHeight = 22.sp)
                Spacer(Modifier.height(10.dp))
                Text("下载后会进行安全校验，再由你确认安装。", color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        is UpdateState.Downloading -> {
            AlertDialog(
                onDismissRequest = {},
                title = { Text("正在下载更新", fontFamily = FontFamily.Serif) },
                text = {
                    Column {
                        Text("新版本 ${state.manifest.versionName} · ${state.progress}%")
                        Spacer(Modifier.height(12.dp))
                        LinearProgressIndicator(progress = { state.progress / 100f }, modifier = Modifier.fillMaxWidth())
                    }
                },
                confirmButton = {},
                dismissButton = {},
                containerColor = PaperLight,
            )
        }

        is UpdateState.Downloaded -> {
            UpdateAlert(
                title = "更新已准备好",
                onDismiss = onDismiss,
                confirm = if (state.needsPermission) "继续安装" else "确认安装",
                onConfirm = { onInstall(state.manifest, state.apk) },
            ) {
                Text(
                    if (state.needsPermission) {
                        "请在系统设置中允许「小坚持」安装应用，然后返回这里继续。更新包已经下载并校验完成。"
                    } else {
                        "新版本 ${state.manifest.versionName} 已通过 SHA-256 安全校验。接下来由 Android 系统确认安装。"
                    },
                    lineHeight = 22.sp,
                )
            }
        }

        UpdateState.UpToDate -> {
            UpdateAlert(
                title = "已经是最新版本",
                onDismiss = onDismiss,
                confirm = "好的",
                onConfirm = onDismiss,
                showDismiss = false,
            ) {
                Text("继续按自己的节奏，认真生活就好。")
            }
        }

        is UpdateState.Error -> {
            UpdateAlert("更新暂时未完成", onDismiss, "稍后再试", onDismiss, dismissLabel = "关闭") {
                Text(state.message, lineHeight = 22.sp)
            }
        }

        else -> {
            Unit
        }
    }
}

@Composable
private fun UpdateAlert(
    title: String,
    onDismiss: () -> Unit,
    confirm: String,
    onConfirm: () -> Unit,
    showDismiss: Boolean = true,
    dismissLabel: String = "暂不更新",
    content: @Composable () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title, fontFamily = FontFamily.Serif) },
        text = { Column { content() } },
        confirmButton = { TextButton(onClick = onConfirm) { Text(confirm) } },
        dismissButton =
            if (showDismiss) {
                { TextButton(onClick = onDismiss) { Text(dismissLabel) } }
            } else {
                null
            },
        containerColor = PaperLight,
    )
}
