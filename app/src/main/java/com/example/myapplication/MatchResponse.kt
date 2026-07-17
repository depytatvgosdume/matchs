package com.example.myapplication.api

import com.google.gson.annotations.SerializedName

// Ответ от API
data class MatchesResponse(
    @SerializedName("matches") val matches: List<MatchApi>
)

// Один матч из API
data class MatchApi(
    @SerializedName("id") val id: Int,
    @SerializedName("utcDate") val utcDate: String,
    @SerializedName("status") val status: String,
    @SerializedName("matchday") val matchday: Int?,
    @SerializedName("homeTeam") val homeTeam: Team,
    @SerializedName("awayTeam") val awayTeam: Team,
    @SerializedName("score") val score: Score,
    @SerializedName("competition") val competition: Competition?
)

data class Competition(
    @SerializedName("name") val name: String
)

data class Team(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("crest") val crest: String?
)

data class Score(
    @SerializedName("fullTime") val fullTime: ScoreDetail?,
    @SerializedName("halfTime") val halfTime: ScoreDetail?
)

data class ScoreDetail(
    @SerializedName("home") val home: Int?,
    @SerializedName("away") val away: Int?
)