package com.xiaojianchi.app.update

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class UpdateManifestTest {
    private val valid =
        """
        {
          "versionCode": 2,
          "versionName": "1.0.1",
          "apkUrl": "https://github.com/honey2306/xiaojianchi/releases/download/v1.0.1/little-steps.apk",
          "sha256": "${"a".repeat(64)}",
          "notes": "修复与改进",
          "publishedAt": "2026-11-06T00:00:00Z"
        }
        """.trimIndent()

    @Test
    fun validManifestIsAccepted() {
        val manifest = UpdateManifest.parse(valid)
        assertEquals(2, manifest.versionCode)
        assertEquals("1.0.1", manifest.versionName)
        assertTrue(manifest.isSafe)
    }

    @Test
    fun insecureApkUrlIsRejected() {
        val manifest = UpdateManifest.parse(valid.replace("https://", "http://"))
        assertFalse(manifest.isSafe)
    }

    @Test
    fun invalidChecksumIsRejected() {
        val manifest = UpdateManifest.parse(valid.replace("a".repeat(64), "not-a-sha"))
        assertFalse(manifest.isSafe)
    }
}
