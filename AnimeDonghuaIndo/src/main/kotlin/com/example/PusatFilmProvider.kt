package com.example

import com.lagradost.cloudstream3.MainAPI
import com.lagradost.cloudstream3.SearchResponse
import com.lagradost.cloudstream3.TvType

class PusatFilmProvider : MainAPI() {
    override var mainUrl = "https://new1.pusatfilm21info.com/"
    override var name = "PusatFilm21"
    override val supportedTypes = setOf(TvType.Movie, TvType.TvSeries)
    override var lang = "id"
    override val hasMainPage = false

    override suspend fun search(query: String): List<SearchResponse> {
        return emptyList()
    }
}
