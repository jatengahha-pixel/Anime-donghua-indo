package com.example

import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.utils.ExtractorLink

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

    override suspend fun load(url: String): LoadResponse? {
        val document = app.get(
            url,
            headers = mapOf("User-Agent" to "Mozilla/5.0")
        ).document

        val title = document.selectFirst(".subheading h2")
            ?.text()?.trim() ?: return null

        val poster = document.selectFirst(".imganime img")?.attr("src")

        val episodes = document.select(".download h4").mapNotNull { heading ->
            val episodeName = heading.text().trim()

            if (episodeName.isBlank() ||
                episodeName.contains("Batch", ignoreCase = true)
            ) return@mapNotNull null

            newEpisode("$url#$episodeName") {
                name = episodeName
                episode = Regex("""Episode\s+(\d+)""")
                    .find(episodeName)?.groupValues?.get(1)?.toIntOrNull()
            }
        }

        return newAnimeLoadResponse(title, url, TvType.Anime) {
            posterUrl = poster
            addEpisodes(DubStatus.Subbed, episodes)
        }
    }

    override suspend fun loadLinks(
        data: String,
        isCasting: Boolean,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
    ): Boolean {
        return false
    }
}
