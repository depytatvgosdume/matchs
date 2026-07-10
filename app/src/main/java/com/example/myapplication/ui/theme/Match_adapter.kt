package com.example.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

// ViewHolder — хранит ссылки на элементы карточки
class MatchViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val tvPeriod: TextView = view.findViewById(R.id.tv_period)
    val tvTeam1: TextView = view.findViewById(R.id.tv_team1)
    val tvTeam2: TextView = view.findViewById(R.id.tv_team2)
    val tvScore1: TextView = view.findViewById(R.id.tv_score1)
    val tvScore2: TextView = view.findViewById(R.id.tv_score2)

}

// Adapter — связывает данные с UI
class MatchAdapter(
    private val matches: List<Match_info>
) : RecyclerView.Adapter<MatchViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_match, parent, false)
        return MatchViewHolder(view)
    }

    override fun onBindViewHolder(holder: MatchViewHolder, position: Int) {
        val match = matches[position]

        holder.tvPeriod.text = match.period
        holder.tvTeam1.text = match.team1
        holder.tvTeam2.text = match.team2
        holder.tvScore1.text = match.score1.toString()
        holder.tvScore2.text = match.score2.toString()


    }

    override fun getItemCount() = matches.size
}