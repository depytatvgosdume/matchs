package com.example.myapplication.api

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface FootballApiService {

    // Получение всех доступных матчей за диапазон дат
    @GET("v4/matches")
    suspend fun getAllMatches(
        @Header("X-Auth-Token") apiKey: String,
        @Query("dateFrom") dateFrom: String? = null,
        @Query("dateTo") dateTo: String? = null
    ): MatchesResponse

    // Получение матчей конкретной лиги
    @GET("v4/competitions/{competitionId}/matches")
    suspend fun getMatches(
        @Path("competitionId") competitionId: String = "PL",
        @Header("X-Auth-Token") apiKey: String
    ): MatchesResponse
}
