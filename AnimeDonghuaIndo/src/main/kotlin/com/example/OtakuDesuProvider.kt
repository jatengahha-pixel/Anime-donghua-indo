package com.example

import com.lagradost.cloudstream3.*
import org.jsoup.Jsoup

class OtakuDesuProvider : MainAPI() {
    override var mainUrl = "https://otakudesu.blog/"
    override var name = "OtakuDesu"
    override var lang = "id"
    override val hasMainPage = false
    override val supportedTypes = setOf(TvType.Anime)

    override suspend fun search(query: String): List<SearchResponse> {
        if (query.isBlank()) return emptyList()

        val document = app.get(
            mainUrl,
            params = mapOf("s" to query),
            headers = mapOf("User-Agent" to "Mozilla/5.0")
        ).document

        return document.select("h2 a").mapNotNull { link ->
            val title = link.text().trim()
            val url = link.attr("href")

            if (title.isBlank() || url.isBlank()) {
                return@mapNotNull null
            }

            newAnimeSearchResponse(title, url, TvType.Anime)
        }
    }
}
