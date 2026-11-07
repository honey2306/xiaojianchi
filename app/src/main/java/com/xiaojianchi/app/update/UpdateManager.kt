package com.xiaojianchi.app.update

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.security.MessageDigest
import java.util.concurrent.TimeUnit

class UpdateManager(
    private val context: Context,
) {
    private val client =
        OkHttpClient
            .Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .callTimeout(90, TimeUnit.SECONDS)
            .build()

    suspend fun checkForUpdate(manifestUrls: List<String>): UpdateManifest =
        withContext(Dispatchers.IO) {
            var lastError: Exception? = null
            for (url in manifestUrls) {
                try {
                    val manifest = fetchManifest(url)
                    require(manifest.isSafe) { "更新清单格式或安全信息无效" }
                    return@withContext manifest
                } catch (error: Exception) {
                    lastError = error
                }
            }
            throw lastError ?: IllegalStateException("暂时无法检查更新")
        }

    private fun fetchManifest(url: String): UpdateManifest {
        client
            .newCall(
                Request
                    .Builder()
                    .url(url)
                    .header("Accept", "application/json")
                    .build(),
            ).execute()
            .use { response ->
                if (!response.isSuccessful) throw IllegalStateException("更新服务返回 ${response.code}")
                val body = response.body ?: throw IllegalStateException("更新清单为空")
                val json =
                    body
                        .byteStream()
                        .readBytes()
                        .also { require(it.size <= 64 * 1024) { "更新清单过大" } }
                        .toString(Charsets.UTF_8)
                return UpdateManifest.parse(json)
            }
    }

    suspend fun download(
        manifest: UpdateManifest,
        onProgress: (Int) -> Unit,
    ): File =
        withContext(Dispatchers.IO) {
            require(manifest.isSafe) { "更新包信息无效" }
            val directory = File(context.cacheDir, "updates").apply { mkdirs() }
            val output = File(directory, "little-steps-${manifest.versionCode}.apk")
            if (output.isFile && sha256(output).equals(manifest.sha256, ignoreCase = true)) {
                onProgress(100)
                return@withContext output
            }
            val partial = File(directory, "${output.name}.part")
            client.newCall(Request.Builder().url(manifest.apkUrl).build()).execute().use { response ->
                if (!response.isSuccessful) throw IllegalStateException("下载失败（${response.code}）")
                val body = response.body ?: throw IllegalStateException("更新包为空")
                val total = body.contentLength()
                val digest = MessageDigest.getInstance("SHA-256")
                var downloaded = 0L
                var lastProgress = -1
                body.byteStream().use { input ->
                    FileOutputStream(partial, false).use { stream ->
                        val buffer = ByteArray(32 * 1024)
                        while (true) {
                            val count = input.read(buffer)
                            if (count < 0) break
                            stream.write(buffer, 0, count)
                            digest.update(buffer, 0, count)
                            downloaded += count
                            val progress = if (total > 0) ((downloaded * 100) / total).toInt().coerceIn(0, 99) else 0
                            if (progress != lastProgress) {
                                onProgress(progress)
                                lastProgress = progress
                            }
                        }
                    }
                }
                require(downloaded > 0) { "更新包为空" }
                val actual = digest.digest().toHex()
                require(MessageDigest.isEqual(actual.toByteArray(), manifest.sha256.lowercase().toByteArray())) { "更新包校验失败，已停止安装" }
            }
            if (output.exists()) output.delete()
            require(partial.renameTo(output)) { "无法保存更新包" }
            onProgress(100)
            output
        }

    fun openInstaller(apk: File): Boolean {
        if (!context.packageManager.canRequestPackageInstalls()) {
            context.startActivity(
                Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:${context.packageName}"))
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
            )
            return false
        }
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", apk)
        val install =
            Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/vnd.android.package-archive")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
        context.startActivity(install)
        return true
    }

    private fun sha256(file: File): String {
        val digest = MessageDigest.getInstance("SHA-256")
        file.inputStream().use { input ->
            val buffer = ByteArray(32 * 1024)
            while (true) {
                val count = input.read(buffer)
                if (count < 0) break
                digest.update(buffer, 0, count)
            }
        }
        return digest.digest().toHex()
    }

    private fun ByteArray.toHex(): String = joinToString("") { "%02x".format(it) }
}
