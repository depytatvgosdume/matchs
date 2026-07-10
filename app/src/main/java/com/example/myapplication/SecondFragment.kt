package com.example.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment

class SecondFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_second, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnGoBack = view.findViewById<Button>(R.id.btn_go_back)

        btnGoBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
        val match = Match(
            matchNumber = 1,
            roundNumber = 1,
            dateUtc = "2021-08-13 19:00:00Z",
            location = "Brentford Community Stadium",
            homeTeam = "Brentford",
            awayTeam = "Arsenal",
            group = "",
            homeTeamScore = 2,
            awayTeamScore = 0
        )
        var tvTeams = view.findViewById<TextView>(R.id.tv_teams)
        var tvScore = view.findViewById<TextView>(R.id.tv_score)
        var tvDate = view.findViewById<TextView>(R.id.tv_date)
        var tvStadion = view.findViewById<TextView>(R.id.tv_stadium)

        tvDate.text = "Время начала: ${match.dateUtc}"
        tvTeams.text = "${match.homeTeam} vs ${match.awayTeam}"
        tvScore.text = "${match.homeTeamScore} : ${match.awayTeamScore}"
        tvStadion.text = "Встретимся на локации: ${match.location}"
    }
}
data class Match(
    val matchNumber: Int,
    val roundNumber: Int,
    val dateUtc: String,
    val location: String,
    val homeTeam: String,
    val awayTeam: String,
    val group: String?,
    val homeTeamScore: Int,
    val awayTeamScore: Int
)