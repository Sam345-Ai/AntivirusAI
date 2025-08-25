package com.antivirusai.security

import android.content.Context
import android.util.Log
import java.nio.ByteBuffer
import java.nio.ByteOrder

class ApkRiskModel(private val ctx: Context) {
    private var tfReady = false
    init {
        try { Class.forName("org.tensorflow.lite.Interpreter"); tfReady = true }
        catch (_: Throwable) { tfReady = false }
    }
    fun predictOrFallback(features: FloatArray): FloatArray {
        return if (tfReady) tryTFLite(features) ?: fallback(features) else fallback(features)
    }
    private fun tryTFLite(features: FloatArray): FloatArray? {
        return try {
            val assetName = "apk_risk_int8.tflite"
            val input = arrayOf(features)
            val output = java.lang.reflect.Array.newInstance(FloatArray(0)::class.java, 1) as Array<FloatArray>
            val optionsClass = Class.forName("org.tensorflow.lite.Interpreter$Options")
            val options = optionsClass.getDeclaredConstructor().newInstance()
            val interpClazz = Class.forName("org.tensorflow.lite.Interpreter")
            val ctor = interpClazz.getDeclaredConstructor(java.nio.ByteBuffer::class.java, optionsClass)
            val model = ctx.assets.open(assetName).readBytes()
            val bb = ByteBuffer.allocateDirect(model.size).order(ByteOrder.nativeOrder())
            bb.put(model); bb.rewind()
            val interpreter = ctor.newInstance(bb, options)
            val run = interpClazz.getMethod("run", Any::class.java, Any::class.java)
            run.invoke(interpreter, input, output)
            output[0]
        } catch (e: Exception) {
            Log.w("ApkRiskModel", "Gagal TFLite, fallback. $e")
            null
        }
    }
    private fun fallback(features: FloatArray): FloatArray {
        val risk = features.sum() / (features.size.coerceAtLeast(1))
        val malware = (0.3f * risk).coerceIn(0f, 1f)
        val gray = (0.4f * risk).coerceIn(0f, 1f)
        val benign = (1f - (malware + gray)).coerceIn(0f, 1f)
        val total = benign + gray + malware
        return floatArrayOf(benign/total, gray/total, malware/total)
    }
}
