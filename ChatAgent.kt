package com.antivirusai.chat

import com.antivirusai.ScanResult

class ChatAgent {
    fun explain(res: ScanResult): String {
        val reasons = if (res.reasons.isEmpty()) "Tidak ada alasan spesifik (skor model)."
                      else res.reasons.joinToString("; ")
        return """
Paket: ${res.pkg}
Label: ${res.label}  |  Skor: ${"%.2f".format(res.score)}  | Sumber: ${res.source}
Alasan: $reasons

Rekomendasi cepat:
- Cabut permission sensitif (SMS, Overlay, Accessibility) bila tidak diperlukan.
- Copot aplikasi dari sumber tidak tepercaya.
- Aktifkan Play Protect & batasi Install unknown sources.
        """.trimIndent()
    }
}
