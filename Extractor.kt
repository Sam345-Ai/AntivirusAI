package com.antivirusai.security

import android.content.Context
import android.content.pm.PackageManager

data class ApkMeta(
    val pkg: String,
    val permissions: Set<String>,
    val exportedReceivers: Int,
    val hasAccessibilityService: Boolean,
    val usesOverlay: Boolean
) {
    fun featuresVector(): FloatArray {
        val smsPair = if (permissions.contains("android.permission.SEND_SMS") &&
            permissions.contains("android.permission.RECEIVE_SMS")) 1f else 0f
        val acc = if (hasAccessibilityService ||
            permissions.contains("android.permission.BIND_ACCESSIBILITY_SERVICE")) 1f else 0f
        val ovl = if (usesOverlay || permissions.contains("android.permission.SYSTEM_ALERT_WINDOW")) 1f else 0f
        val many = if (exportedReceivers > 5) 1f else 0f
        return floatArrayOf(smsPair, acc, ovl, many)
    }
}

class Extractor(private val ctx: Context) {
    fun extract(pkg: String): ApkMeta {
        val pm = ctx.packageManager
        val perms = try {
            val pi = pm.getPackageInfo(pkg, PackageManager.GET_PERMISSIONS)
            (pi.requestedPermissions?.toList() ?: emptyList()).toSet()
        } catch (_: Exception) { emptySet() }
        val exportedReceivers = 0
        val hasAcc = perms.contains("android.permission.BIND_ACCESSIBILITY_SERVICE")
        val overlay = perms.contains("android.permission.SYSTEM_ALERT_WINDOW")
        return ApkMeta(pkg, perms, exportedReceivers, hasAcc, overlay)
    }
}
