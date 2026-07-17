package com.example.myapplication

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class FavoritesFragment : Fragment() {

    private val PREFS_NAME = "match_prefs"
    private val KEY_MATCHES = "cached_matches"
    private var favAdapter: MatchAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_favorites, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_fav)
        val tvEmpty = view.findViewById<TextView>(R.id.tv_empty_fav)
        val btnBack = view.findViewById<ImageButton>(R.id.btn_back_fav)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        loadFavorites(recyclerView, tvEmpty)
    }

    private fun loadFavorites(recyclerView: RecyclerView, tvEmpty: TextView) {
        val prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val cachedJson = prefs.getString(KEY_MATCHES, null)
        
        if (cachedJson != null) {
            val type = object : TypeToken<List<Match_info>>() {}.type
            val allMatches: List<Match_info> = Gson().fromJson(cachedJson, type)
            val favMatches = allMatches.filter { it.isFavorite }

            if (favMatches.isEmpty()) {
                tvEmpty.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            } else {
                tvEmpty.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
                
                favAdapter = MatchAdapter(
                    matches = favMatches,
                    showCarousel = false, // Отключаем карусель и фильтры для этого экрана
                    onFavoriteChanged = {
                        // При удалении из избранного прямо здесь - обновляем кэш и список
                        saveUpdatedFavorites(favAdapter?.getAllData() ?: emptyList())
                        // Если список стал пустым после удаления, показываем заглушку
                        if (favAdapter?.itemCount == 0) {
                            tvEmpty.visibility = View.VISIBLE
                        }
                    }
                )
                recyclerView.adapter = favAdapter
            }
        } else {
            tvEmpty.visibility = View.VISIBLE
        }
    }

    private fun saveUpdatedFavorites(currentFavs: List<Match_info>) {
        val prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val cachedJson = prefs.getString(KEY_MATCHES, null)
        
        if (cachedJson != null) {
            val type = object : TypeToken<List<Match_info>>() {}.type
            val allMatches: List<Match_info> = Gson().fromJson(cachedJson, type)
            
            // Обновляем статусы в общем списке на основе текущего экрана избранного
            val updatedAllMatches = allMatches.map { match ->
                val currentFav = currentFavs.find { it.team1 == match.team1 && it.team2 == match.team2 }
                if (currentFav != null) {
                    match.copy(isFavorite = currentFav.isFavorite)
                } else {
                    // Если матча нет в текущем списке избранного, значит он либо никогда там не был,
                    // либо его только что удалили.
                    match.copy(isFavorite = false)
                }
            }
            
            prefs.edit().putString(KEY_MATCHES, Gson().toJson(updatedAllMatches)).apply()
        }
    }
}
