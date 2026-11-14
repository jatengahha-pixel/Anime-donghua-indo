package com.example

import com.lagradost.cloudstream3.MainAPI
import com.lagradost.cloudstream3.SearchResponse
import com.lagradost.cloudstream3.TvType

class OtakuDesuProvider : MainAPI() {
    override var mainUrl = "https://otakudesu.blog/"
    override var name = "OtakuDesu"
    override val supportedTypes = setOf(TvType.Anime)
    override var lang = "id"
    override val hasMainPage = false

    override suspend fun search(query: String): List<SearchResponse> {
        return emptyList()
    }
}
