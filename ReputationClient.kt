
package com.antivirusai.net

import okhttp3.*
import com.squareup.moshi.*

data class RepResponse(val found: Boolean, val label: String? = null, val source: String? = null, val risk: Double? = null)

class ReputationClient(private val baseUrl: String) {
    private val client = OkHttpClient()
    private val moshi = Moshi.Builder().build()
    private val adapter = moshi.adapter(RepResponse::class.java)

    fun reputation(sha256: String, pkg: String): RepResponse {
        val json = """{"sha256":"$sha256","pkg":"$pkg"}""".trimIndent()
        val req = Request.Builder()
            .url("${baseUrl.trimEnd('/')}/reputation")
            .post(RequestBody.create(MediaType.parse("application/json"), json))
            .build()
        client.newCall(req).execute().use { resp ->
            val body = resp.body()?.string() ?: return RepResponse(false)
            return adapter.fromJson(body) ?: RepResponse(false)
        }
    }
}
