package com.example.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class FirstFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_first, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_matches)

        // Настройка RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Создаём список матчей
        val mass_data = listOf(
            Match_info("1-й тайм", "Аякс", "АЕК Ларнака", 0, 0),
            Match_info("2-й тайм", "Барселона", "Реал Мадрид", 2, 1),
            Match_info("1-й тайм", "Бавария", "Боруссия", 1, 1),
            Match_info("Матч окончен", "Ливерпуль", "Манчестер Сити", 3, 2),
            Match_info("1-й тайм", "ПСЖ", "Марсель", 0, 1),
            Match_info("2-й тайм", "Ювентус", "Интер", 1, 0),
            Match_info("1-й тайм", "Челси", "Арсенал", 2, 2),
            Match_info("Матч окончен", "Тоттенхэм", "Манчестер Юнайтед", 4, 1),
            Match_info("1-й тайм", "Милан", "Наполи", 0, 0),
            Match_info("2-й тайм", "Атлетико", "Севилья", 1, 1)
        )

        // Устанавливаем адаптер
        recyclerView.adapter = MatchAdapter(mass_data)
    }
}