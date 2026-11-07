package com.xiaojianchi.app.update

import org.json.JSONObject
import java.net.URI

/** The release manifest lives in a GitHub Release, never in the installed APK. */
data class UpdateManifest(
    val versionCode: Int,
    val versionName: String,
    val apkUrl: String,
    val sha256: String,
    val notes: String,
    val publishedAt: String,
) {
    val isSafe: Boolean
        get() =
            versionCode > 0 &&
                versionName.isNotBlank() &&
                runCatching { URI(apkUrl).let { it.scheme == "https" && it.userInfo == null && it.host == "github.com" } }.getOrDefault(false) &&
                sha256.matches(Regex("[0-9a-fA-F]{64}"))

    companion object {
        fun parse(json: String): UpdateManifest {
            val root = JSONObject(json)
            return UpdateManifest(
                versionCode = root.getInt("versionCode"),
                versionName = root.getString("versionName"),
                apkUrl = root.getString("apkUrl"),
                sha256 = root.getString("sha256"),
                notes = root.optString("notes", "包含体验改进与问题修复。"),
                publishedAt = root.optString("publishedAt", ""),
            )
        }
    }
}
