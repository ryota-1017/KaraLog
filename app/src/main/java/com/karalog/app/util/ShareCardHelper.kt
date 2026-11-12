package com.karalog.app.util

import android.content.Context
import android.content.Intent
import android.graphics.*
import androidx.core.content.FileProvider
import com.karalog.app.data.model.SingingRecordWithSong
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

object ShareCardHelper {

    fun generateAndShareSetlist(
        context: Context,
        records: List<SingingRecordWithSong>,
        sessionDate: Long = System.currentTimeMillis()
    ) {
        if (records.isEmpty()) return

        val width = 1080
        val height = 1920
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // 1. Background (True Black)
        canvas.drawColor(Color.parseColor("#050505"))

        // Subtle outer border (Instagram frame)
        val framePaint = Paint().apply {
            color = Color.parseColor("#1F1F1F")
            style = Paint.Style.STROKE
            strokeWidth = 3f
            isAntiAlias = true
        }
        canvas.drawRect(50f, 60f, (width - 50).toFloat(), (height - 60).toFloat(), framePaint)

        // Paints
        val titlePaint = Paint().apply {
            color = Color.parseColor("#FFFFFF")
            textSize = 54f
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
            isAntiAlias = true
            letterSpacing = 0.15f
        }

        val subPaint = Paint().apply {
            color = Color.parseColor("#8E8E93")
            textSize = 28f
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
            isAntiAlias = true
            letterSpacing = 0.1f
        }

        val statValuePaint = Paint().apply {
            color = Color.parseColor("#FFFFFF")
            textSize = 72f
            typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
            isAntiAlias = true
        }

        val statLabelPaint = Paint().apply {
            color = Color.parseColor("#737373")
            textSize = 24f
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
            isAntiAlias = true
            letterSpacing = 0.08f
        }

        val dividerPaint = Paint().apply {
            color = Color.parseColor("#262626")
            strokeWidth = 2f
            isAntiAlias = true
        }

        // Header
        canvas.drawText("KARALOG", 100f, 150f, titlePaint)
        val dateFormat = SimpleDateFormat("yyyy.MM.dd  EEEE", Locale.ENGLISH)
        canvas.drawText("SESSION REPORT  /  " + dateFormat.format(Date(sessionDate)).uppercase(Locale.ENGLISH), 100f, 205f, subPaint)

        canvas.drawLine(100f, 250f, (width - 100).toFloat(), 250f, dividerPaint)

        // Stats summary
        val count = records.size
        val avgScore = String.format(Locale.ENGLISH, "%.2f", records.map { it.record.score }.average())
        val maxScoreRecord = records.maxByOrNull { it.record.score }
        val maxScore = if (maxScoreRecord != null) String.format(Locale.ENGLISH, "%.3f", maxScoreRecord.record.score) else "-"

        // Stat 1: Total Songs
        canvas.drawText(count.toString(), 100f, 340f, statValuePaint)
        canvas.drawText("TOTAL SONGS", 100f, 385f, statLabelPaint)

        // Stat 2: Avg Score
        canvas.drawText(avgScore, 420f, 340f, statValuePaint)
        canvas.drawText("AVERAGE SCORE", 420f, 385f, statLabelPaint)

        // Stat 3: Best Score
        canvas.drawText(maxScore, 750f, 340f, statValuePaint)
        canvas.drawText("TOP SCORE", 750f, 385f, statLabelPaint)

        canvas.drawLine(100f, 430f, (width - 100).toFloat(), 430f, dividerPaint)

        // Tracklist
        val songNumPaint = Paint().apply {
            color = Color.parseColor("#737373")
            textSize = 30f
            typeface = Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL)
            isAntiAlias = true
        }

        val songTitlePaint = Paint().apply {
            color = Color.parseColor("#FFFFFF")
            textSize = 34f
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
            isAntiAlias = true
        }

        val songArtistPaint = Paint().apply {
            color = Color.parseColor("#8E8E93")
            textSize = 26f
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
            isAntiAlias = true
        }

        val songScorePaint = Paint().apply {
            color = Color.parseColor("#FFFFFF")
            textSize = 38f
            typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
            isAntiAlias = true
            textAlign = Paint.Align.RIGHT
        }

        var currentY = 510f
        val maxItemsToShow = 14
        val displayList = records.take(maxItemsToShow)

        displayList.forEachIndexed { index, item ->
            val numStr = String.format(Locale.ENGLISH, "%02d", index + 1)
            canvas.drawText(numStr, 100f, currentY, songNumPaint)

            // Truncate title if too long
            var titleText = item.songTitle
            if (titleText.length > 18) {
                titleText = titleText.take(17) + "…"
            }
            canvas.drawText(titleText, 170f, currentY, songTitlePaint)

            var artistText = item.songArtist
            if (artistText.isNotEmpty()) {
                if (artistText.length > 14) artistText = artistText.take(13) + "…"
                canvas.drawText("· $artistText", 520f, currentY, songArtistPaint)
            }

            val scoreStr = String.format(Locale.ENGLISH, "%.3f", item.record.score)
            canvas.drawText(scoreStr, (width - 100).toFloat(), currentY, songScorePaint)

            currentY += 82f
        }

        if (records.size > maxItemsToShow) {
            val remain = records.size - maxItemsToShow
            canvas.drawText("... and $remain more tracks", 170f, currentY, subPaint)
        }

        // Footer
        canvas.drawLine(100f, (height - 180).toFloat(), (width - 100).toFloat(), (height - 180).toFloat(), dividerPaint)
        val footerPaint = Paint().apply {
            color = Color.parseColor("#5A5A5A")
            textSize = 24f
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
            isAntiAlias = true
            letterSpacing = 0.2f
        }
        canvas.drawText("RECORDED WITH KARALOG  ·  MINIMAL KARAOKE LOG", 100f, (height - 120).toFloat(), footerPaint)

        // Save to cache directory
        try {
            val cachePath = File(context.cacheDir, "images")
            cachePath.mkdirs()
            val file = File(cachePath, "karalog_setlist.png")
            val stream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream.close()

            val contentUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "image/png"
                putExtra(Intent.EXTRA_STREAM, contentUri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            val chooser = Intent.createChooser(shareIntent, "セトリ画像をシェア").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(chooser)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
