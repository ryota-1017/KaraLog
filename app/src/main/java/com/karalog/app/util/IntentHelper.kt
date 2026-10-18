package com.karalog.app.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import java.net.URLEncoder

object IntentHelper {
    fun openSpotifySearch(context: Context, query: String) {
        val encoded = URLEncoder.encode(query, "UTF-8")
        val uri = Uri.parse("spotify:search:$encoded")
        val intent = Intent(Intent.ACTION_VIEW, uri).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK }
        try {
            context.startActivity(intent)
        } catch (_: Exception) {
            val webUri = Uri.parse("https://open.spotify.com/search/$encoded")
            context.startActivity(Intent(Intent.ACTION_VIEW, webUri).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK })
        }
    }

    fun openYouTubeMusicSearch(context: Context, query: String) {
        val encoded = URLEncoder.encode(query, "UTF-8")
        val webUri = Uri.parse("https://music.youtube.com/search?q=$encoded")
        val intent = Intent(Intent.ACTION_VIEW, webUri).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK }
        try {
            context.startActivity(intent)
        } catch (_: Exception) {}
    }
}