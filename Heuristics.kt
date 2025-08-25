package com.antivirusai.security

object Heuristics {
    data class Hint(val key: String, val weight: Float, val reason: String)
    fun evaluate(meta: ApkMeta): List<Hint> {
        val out = mutableListOf<Hint>()
        val p = meta.permissions
        if (p.contains("android.permission.SEND_SMS") && p.contains("android.permission.RECEIVE_SMS"))
            out += Hint("sms_pair", 0.15f, "Akses kirim/terima SMS berisiko tagihan/phishing.")
        if (p.contains("android.permission.SYSTEM_ALERT_WINDOW"))
            out += Hint("overlay", 0.12f, "Overlay bisa dipakai untuk phishing layar.")
        if (p.contains("android.permission.BIND_ACCESSIBILITY_SERVICE") || meta.hasAccessibilityService)
            out += Hint("acc_service", 0.20f, "Akses Accessibility rawan penyalahgunaan auto-click.")
        if (meta.exportedReceivers > 5)
            out += Hint("many_receivers", 0.08f, "Receiver diekspos terlalu banyak.")
        return out
    }
}
