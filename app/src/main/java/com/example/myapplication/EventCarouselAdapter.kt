package com.example.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class EventCarouselAdapter(private val events: List<Match_info>) : RecyclerView.Adapter<EventCarouselAdapter.EventViewHolder>() {

    class EventViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvDate: TextView = view.findViewById(R.id.tv_event_date)
        val tvTeam1: TextView = view.findViewById(R.id.tv_event_team1)
        val tvTeam2: TextView = view.findViewById(R.id.tv_event_team2)
        val btnP1: Button = view.findViewById(R.id.btn_p1)
        val btnX: Button = view.findViewById(R.id.btn_x)
        val btnP2: Button = view.findViewById(R.id.btn_p2)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_event_carousel, parent, false)
        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        if (events.isEmpty()) return
        
        val realPosition = position % events.size
        val match = events[realPosition]
        
        holder.tvDate.text = match.period
        holder.tvTeam1.text = match.team1
        holder.tvTeam2.text = match.team2
        
        holder.btnP1.setOnClickListener { /* Обработка */ }
        holder.btnX.setOnClickListener { /* Обработка */ }
        holder.btnP2.setOnClickListener { /* Обработка */ }
    }

    override fun getItemCount(): Int = if (events.isEmpty()) 0 else Int.MAX_VALUE
}
