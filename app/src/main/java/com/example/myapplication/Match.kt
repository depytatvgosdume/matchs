package com.example.myapplication

data class Match_info(
    val period: String,
    val status: String,
    val team1: String,
    val team2: String,
    val score1: Int,
    val score2: Int,
    val crest1: String? = null,
    val crest2: String? = null,
    var isFavorite: Boolean = false,
    val competitionName: String? = null // Добавили название турнира
)
