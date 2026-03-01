package edu.nd.pmcburne.hwapp.one.data.remote

import retrofit2.http.GET
import retrofit2.http.Path

interface NcaaApiService {
    @GET("scoreboard/basketball-{gender}/d1/{yyyy}/{mm}/{dd}")
    suspend fun getScoreboard(
        @Path("gender") gender: String,
        @Path("yyyy") year: String,
        @Path("mm") month: String,
        @Path("dd") day: String
    ): ScoreboardResponse
}
