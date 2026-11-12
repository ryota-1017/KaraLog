package com.karalog.app.util

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.util.regex.Pattern

data class OcrScoreResult(
    val score: Double?,
    val pitchAccuracy: Double? = null,
    val rawExtractedText: String = ""
)

object ScoreOcrHelper {

    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    fun recognizeScoreFromUri(
        context: Context,
        imageUri: Uri,
        onSuccess: (OcrScoreResult) -> Unit,
        onError: (Exception) -> Unit
    ) {
        try {
            val image = InputImage.fromFilePath(context, imageUri)
            processImage(image, onSuccess, onError)
        } catch (e: Exception) {
            onError(e)
        }
    }

    fun recognizeScoreFromBitmap(
        bitmap: Bitmap,
        onSuccess: (OcrScoreResult) -> Unit,
        onError: (Exception) -> Unit
    ) {
        try {
            val image = InputImage.fromBitmap(bitmap, 0)
            processImage(image, onSuccess, onError)
        } catch (e: Exception) {
            onError(e)
        }
    }

    private fun processImage(
        image: InputImage,
        onSuccess: (OcrScoreResult) -> Unit,
        onError: (Exception) -> Unit
    ) {
        recognizer.process(image)
            .addOnSuccessListener { visionText ->
                val fullText = visionText.text
                var foundScore: Double? = null
                var foundPitch: Double? = null

                // 1. スコア検索 (例: 94.521, 98.100, 100.000, 89.4 等)
                val scorePattern = Pattern.compile("""\b(100(?:\.000?)?|[5-9][0-9]\.[0-9]{1,3})\b""")
                val matcher = scorePattern.matcher(fullText)
                val candidates = mutableListOf<Double>()
                while (matcher.find()) {
                    val s = matcher.group(1)?.toDoubleOrNull()
                    if (s != null && s in 50.0..100.0) {
                        candidates.add(s)
                    }
                }

                // 2. 音程正確率検索 (例: 音程 89%, 92%)
                val pitchPattern = Pattern.compile("""(?:音程|正解率|率)\D*([5-9][0-9](?:\.[0-9])?)\s*%?""")
                val pitchMatcher = pitchPattern.matcher(fullText)
                if (pitchMatcher.find()) {
                    foundPitch = pitchMatcher.group(1)?.toDoubleOrNull()
                }

                // 最も採点スコアらしい候補（小数第2位〜3位があるものを最優先、なければ最大値）
                val detailed = candidates.find { (it * 1000 % 1.0) != 0.0 || (it * 100 % 1.0) != 0.0 }
                foundScore = detailed ?: candidates.maxOrNull()

                onSuccess(
                    OcrScoreResult(
                        score = foundScore,
                        pitchAccuracy = foundPitch,
                        rawExtractedText = fullText
                    )
                )
            }
            .addOnFailureListener { e ->
                onError(e)
            }
    }
}
