package com.karalog.app.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import java.net.URLEncoder

object IntentHelper {

    fun openSpotify(context: Context, title: String, artist: String = "", customUrl: String = "") {
        if (customUrl.isNotEmpty()) {
            openUrl(context, customUrl)
            return
        }
        val query = if (artist.isNotEmpty()) "$title $artist" else title
        val encoded = URLEncoder.encode(query, "UTF-8")
        val appUri = Uri.parse("spotify:search:$encoded")
        val intent = Intent(Intent.ACTION_VIEW, appUri).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        try {
            context.startActivity(intent)
        } catch (_: Exception) {
            val webUri = Uri.parse("https://open.spotify.com/search/$encoded")
            openUrl(context, webUri.toString())
        }
    }

    fun openYouTubeMusic(context: Context, title: String, artist: String = "", customUrl: String = "") {
        if (customUrl.isNotEmpty()) {
            openUrl(context, customUrl)
            return
        }
        val query = if (artist.isNotEmpty()) "$title $artist" else title
        val encoded = URLEncoder.encode(query, "UTF-8")
        val webUri = Uri.parse("https://music.youtube.com/search?q=$encoded")
        val intent = Intent(Intent.ACTION_VIEW, webUri).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "YouTube Musicを開けませんでした", Toast.LENGTH_SHORT).show()
        }
    }

    fun openYouTube(context: Context, title: String, artist: String = "", customUrl: String = "") {
        if (customUrl.isNotEmpty()) {
            openUrl(context, customUrl)
            return
        }
        val query = if (artist.isNotEmpty()) "$title $artist" else title
        val encoded = URLEncoder.encode(query, "UTF-8")
        val webUri = Uri.parse("https://www.youtube.com/results?search_query=$encoded")
        val intent = Intent(Intent.ACTION_VIEW, webUri).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "YouTubeを開けませんでした", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openUrl(context: Context, url: String) {
        try {
            val uri = Uri.parse(url)
            val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (_: Exception) {
            Toast.makeText(context, "リンクを開けませんでした", Toast.LENGTH_SHORT).show()
        }
    }
}
