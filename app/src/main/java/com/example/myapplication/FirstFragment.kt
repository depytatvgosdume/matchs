package com.example.myapplication

import android.os.Bundle
import android.widget.ImageButton
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import android.widget.ImageView
import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.api.RetrofitClient
import kotlinx.coroutines.launch

class FirstFragment : Fragment() {

    private val API_KEY = "df1379923a254649b4013d2d8a1759ab"
    private var matchAdapter: MatchAdapter? = null
    private val PREFS_NAME = "match_prefs"
    private val KEY_MATCHES = "cached_matches"

    private val dummyEvents = listOf(
        Match_info("20.07 18:00", "SCHEDULED", "Arsenal", "Chelsea", 0, 0),
        Match_info("21.07 20:45", "SCHEDULED", "Real Madrid", "Barcelona", 0, 0),
        Match_info("22.07 19:30", "SCHEDULED", "Juventus", "Inter", 0, 0),
        Match_info("23.07 21:00", "SCHEDULED", "Bayern", "Dortmund", 0, 0)
    )

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
        val progressBar = view.findViewById<ProgressBar>(R.id.progress_bar)
        val tvError = view.findViewById<TextView>(R.id.tv_error)
        val menuButton = view.findViewById<ImageButton>(R.id.menu)
        val searchButton = view.findViewById<ImageButton>(R.id.search)
        val searchView = view.findViewById<SearchView>(R.id.search_view)
        val logo = view.findViewById<ImageView>(R.id.logo)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Инициализация адаптера
        matchAdapter = MatchAdapter(
            matches = emptyList(),
            carouselEvents = dummyEvents,
            onResultCountChanged = { count -> updateNotFoundVisibility(count) },
            onFavoriteChanged = { 
                // Сохраняем весь список в кэш при изменении статуса "Избранное"
                matchAdapter?.let { adapter ->
                    // Здесь небольшая хитрость: нам нужно получить полный список из адаптера
                    // Для этого мы временно добавим метод или будем использовать текущее состояние
                    saveMatchesToCache(getCurrentMatchesFromAdapter())
                }
            }
        )
        recyclerView.adapter = matchAdapter

        loadCachedMatches()

        menuButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ThreeFragment())
                .addToBackStack(null)
                .commit()
        }

        searchButton.setOnClickListener {
            if (searchView.visibility == View.GONE) {
                searchView.visibility = View.VISIBLE
                logo.visibility = View.GONE
                menuButton.visibility = View.GONE
                searchView.isIconified = false
                searchView.requestFocus()
            } else {
                searchView.visibility = View.GONE
                logo.visibility = View.VISIBLE
                menuButton.visibility = View.VISIBLE
                searchView.setQuery("", false)
                matchAdapter?.filter(query = "")
            }
        }

        val searchText = searchView.findViewById<TextView>(androidx.appcompat.R.id.search_src_text)
        searchText.setTextColor(android.graphics.Color.WHITE)
        searchText.setHintTextColor(android.graphics.Color.LTGRAY)

        val searchIcon = searchView.findViewById<ImageView>(androidx.appcompat.R.id.search_mag_icon)
        searchIcon.setImageDrawable(null)
        searchIcon.visibility = View.GONE

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false
            override fun onQueryTextChange(newText: String?): Boolean {
                matchAdapter?.filter(query = newText ?: "")
                return true
            }
        })

        loadMatches(recyclerView, progressBar, tvError)
    }

    private fun getCurrentMatchesFromAdapter(): List<Match_info> {
        return matchAdapter?.getAllData() ?: emptyList()
    }

    private fun formatMatchDate(utcDate: String): String {
        return try {
            val inputFormat = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", java.util.Locale.US)
            inputFormat.timeZone = java.util.TimeZone.getTimeZone("UTC")
            val date = inputFormat.parse(utcDate)
            val outputFormat = java.text.SimpleDateFormat("dd.MM HH:mm", java.util.Locale.getDefault())
            if (date != null) outputFormat.format(date) else utcDate
        } catch (e: Exception) {
            utcDate
        }
    }

    private fun updateNotFoundVisibility(count: Int) {
        val tvNotFound = view?.findViewById<TextView>(R.id.tv_not_found)
        if (count == 0) {
            tvNotFound?.visibility = View.VISIBLE
        } else {
            tvNotFound?.visibility = View.GONE
        }
    }

    private fun loadCachedMatches() {
        val prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val cachedJson = prefs.getString(KEY_MATCHES, null)
        if (cachedJson != null) {
            try {
                val type = object : TypeToken<List<Match_info>>() {}.type
                val matches: List<Match_info> = Gson().fromJson(cachedJson, type)
                matchAdapter?.updateData(matches)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun saveMatchesToCache(matches: List<Match_info>) {
        if (matches.isEmpty()) return // Не затираем кэш пустым списком от лоадера
        val prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = Gson().toJson(matches)
        prefs.edit().putString(KEY_MATCHES, json).apply()
    }

    private fun loadMatches(recyclerView: RecyclerView, progressBar: ProgressBar, tvError: TextView) {
        if (matchAdapter == null || (matchAdapter?.itemCount ?: 0) <= 2) {
            progressBar.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        }
        tvError.visibility = View.GONE

        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US)
        val calendar = java.util.Calendar.getInstance()
        calendar.add(java.util.Calendar.DAY_OF_YEAR, -4)
        val dateFrom = sdf.format(calendar.time)
        calendar.add(java.util.Calendar.DAY_OF_YEAR, 9)
        val dateTo = sdf.format(calendar.time)

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.getAllMatches(
                    apiKey = API_KEY,
                    dateFrom = dateFrom,
                    dateTo = dateTo
                )
                
                // Чтобы не потерять статус "Избранное" при обновлении из сети, 
                // нужно сравнить новые данные с кэшем
                val cachedMatches = getCachedMatchesList()
                
                val matches = response.matches.map { matchApi ->
                    val isFav = cachedMatches.find { it.team1 == matchApi.homeTeam.name && it.team2 == matchApi.awayTeam.name }?.isFavorite ?: false
                    Match_info(
                        period = formatMatchDate(matchApi.utcDate),
                        status = matchApi.status,
                        team1 = matchApi.homeTeam.name,
                        team2 = matchApi.awayTeam.name,
                        score1 = matchApi.score.fullTime?.home ?: 0,
                        score2 = matchApi.score.fullTime?.away ?: 0,
                        crest1 = matchApi.homeTeam.crest,
                        crest2 = matchApi.awayTeam.crest,
                        competitionName = matchApi.competition?.name,
                        isFavorite = isFav
                    )
                }
                saveMatchesToCache(matches)
                progressBar.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
                matchAdapter?.updateData(matches)
            } catch (e: Exception) {
                progressBar.visibility = View.GONE
                if (matchAdapter != null && matchAdapter!!.itemCount > 2) {
                    Toast.makeText(requireContext(), "Ошибка обновления", Toast.LENGTH_SHORT).show()
                    recyclerView.visibility = View.VISIBLE
                } else {
                    tvError.visibility = View.VISIBLE
                    tvError.text = "Ошибка загрузки"
                }
                e.printStackTrace()
            }
        }
    }

    private fun getCachedMatchesList(): List<Match_info> {
        val prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val cachedJson = prefs.getString(KEY_MATCHES, null)
        return if (cachedJson != null) {
            val type = object : TypeToken<List<Match_info>>() {}.type
            Gson().fromJson(cachedJson, type)
        } else emptyList()
    }
}
