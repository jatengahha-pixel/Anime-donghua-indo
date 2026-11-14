package com.example

import com.lagradost.cloudstream3.*
import org.json.JSONObject
import org.jsoup.Jsoup

class AniChinProvider : MainAPI() {
    override var mainUrl = "https://www.bilibili.com/"
    override var name = "Bilibili"
    override var lang = "id"
    override val hasMainPage = false
    override val supportedTypes = setOf(TvType.Anime, TvType.Movie)

    override suspend fun search(query: String): List<SearchResponse> {
        if (query.isBlank()) return emptyList()

        val response = app.get(
            "https://api.bilibili.com/x/web-interface/search/all/v2",
            params = mapOf("keyword" to query),
            headers = mapOf(
                "User-Agent" to "Mozilla/5.0",
                "Referer" to "https://www.bilibili.com/"
            )
        ).text

        val root = JSONObject(response)
        if (root.optInt("code", -1) != 0) return emptyList()

        val results = root.getJSONObject("data").getJSONArray("result")
        val videos = mutableListOf<SearchResponse>()

        for (i in 0 until results.length()) {
            val group = results.getJSONObject(i)
            if (group.optString("result_type") != "video") continue

            val items = group.optJSONArray("data") ?: continue

            for (j in 0 until items.length()) {
                val item = items.getJSONObject(j)
                val bvid = item.optString("bvid")
                val rawTitle = item.optString("title")
                val title = Jsoup.parse(rawTitle).text()

                if (bvid.isBlank() || title.isBlank()) continue

                videos.add(
                    newAnimeSearchResponse(
                        title,
                        "https://www.bilibili.com/video/$bvid",
                        TvType.Anime
                    )
                )
            }
        }

        return videos
    }
}
