package com.example

import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.utils.*

class AniChinProvider : MainAPI() {
    override var mainUrl = "https://anichin.moe/"
    override var name = "AniChin Donghua"
    override var lang = "id"
    override val hasMainPage = false
    override val supportedTypes = setOf(TvType.Anime)

    private val userAgent =
        "Mozilla/5.0 (Linux; Android 10; Mobile) AppleWebKit/537.36 Chrome/120.0.0.0 Mobile Safari/537.36"

    override suspend fun search(query: String): List<SearchResponse> {
        val document = app.get(
            mainUrl,
            params = mapOf("s" to query),
            headers = mapOf("User-Agent" to userAgent)
        ).document

        return document.select("div.bsx").mapNotNull { item ->
            val link = item.selectFirst("a.tip") ?: return@mapNotNull null
            val title = link.attr("title").ifBlank {
                item.selectFirst(".tt")?.text()?.trim().orEmpty()
            }
            val url = link.attr("href")
            if (title.isBlank() || url.isBlank()) return@mapNotNull null


            newAnimeSearchResponse(title, url, TvType.Anime) {
                posterUrl = null
            }
        }
    }
}
