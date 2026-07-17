package com.example.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.widget.ViewPager2
import coil.load
import coil.decode.SvgDecoder
import coil.ImageLoader

// ViewHolder для матча
class MatchViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val tvPeriod: TextView = view.findViewById(R.id.tv_period)
    val tvTeam1: TextView = view.findViewById(R.id.tv_team1)
    val tvTeam2: TextView = view.findViewById(R.id.tv_team2)
    val tvScore1: TextView = view.findViewById(R.id.tv_score1)
    val tvScore2: TextView = view.findViewById(R.id.tv_score2)
    val ivCrest1: ImageView = view.findViewById(R.id.iv_crest1)
    val ivCrest2: ImageView = view.findViewById(R.id.iv_crest2)
    val ivFavorite: ImageView = view.findViewById(R.id.iv_favorite)
    val tvCompetition: TextView = view.findViewById(R.id.tv_competition)
}

// ViewHolder для карусели
class CarouselViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val viewPager: ViewPager2 = view.findViewById(R.id.vp_events)
}

// ViewHolder для фильтров
class FiltersViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val filterAll: TextView = view.findViewById(R.id.filter_all)
    val filterScheduled: TextView = view.findViewById(R.id.filter_scheduled)
    val filterInPlay: TextView = view.findViewById(R.id.filter_in_play)
    val filterFinished: TextView = view.findViewById(R.id.filter_finished)
}

class MatchAdapter(
    private var matches: List<Match_info>,
    private var carouselEvents: List<Match_info> = emptyList(),
    private val showCarousel: Boolean = true, // Новое поле для управления каруселью
    private val onResultCountChanged: (count: Int) -> Unit = {},
    private val onFavoriteChanged: () -> Unit = {} // Колбэк для сохранения в кэш
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var matchesFull: List<Match_info> = ArrayList(matches)
    private var imageLoader: ImageLoader? = null
    private var currentStatus: String = "Все"
    private var currentQuery: String = ""

    companion object {
        private const val TYPE_CAROUSEL = 0
        private const val TYPE_FILTERS = 1
        private const val TYPE_MATCH = 2
    }

    private fun getImageLoader(context: android.content.Context): ImageLoader {
        if (imageLoader == null) {
            imageLoader = ImageLoader.Builder(context)
                .components { add(SvgDecoder.Factory()) }
                .okHttpClient {
                    okhttp3.OkHttpClient.Builder()
                        .hostnameVerifier { _, _ -> true }
                        .build()
                }
                .memoryCachePolicy(coil.request.CachePolicy.ENABLED)
                .diskCachePolicy(coil.request.CachePolicy.ENABLED)
                .build()
        }
        return imageLoader!!
    }

    private fun isSearchActive(): Boolean = currentQuery.isNotEmpty()

    override fun getItemViewType(position: Int): Int {
        if (!showCarousel) return TYPE_MATCH // Для экрана избранного только матчи
        
        return if (isSearchActive()) {
            if (position == 0) TYPE_FILTERS else TYPE_MATCH
        } else {
            when (position) {
                0 -> TYPE_CAROUSEL
                1 -> TYPE_FILTERS
                else -> TYPE_MATCH
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_CAROUSEL -> CarouselViewHolder(inflater.inflate(R.layout.item_carousel_container, parent, false))
            TYPE_FILTERS -> FiltersViewHolder(inflater.inflate(R.layout.item_filters, parent, false))
            else -> MatchViewHolder(inflater.inflate(R.layout.item_match, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is CarouselViewHolder -> {
                if (carouselEvents.isNotEmpty()) {
                    val eventAdapter = EventCarouselAdapter(carouselEvents)
                    holder.viewPager.adapter = eventAdapter
                    val middlePosition = (Int.MAX_VALUE / 2) - ((Int.MAX_VALUE / 2) % carouselEvents.size)
                    holder.viewPager.setCurrentItem(middlePosition, false)
                }
            }
            is FiltersViewHolder -> {
                updateFilterSelection(holder)
                
                val listener = View.OnClickListener { v ->
                    val status = when (v.id) {
                        R.id.filter_all -> "Все"
                        R.id.filter_scheduled -> "Запланированы"
                        R.id.filter_in_play -> "Идут"
                        R.id.filter_finished -> "Завершены"
                        else -> "Все"
                    }
                    filter(status = status)
                    updateFilterSelection(holder)
                }
                
                holder.filterAll.setOnClickListener(listener)
                holder.filterScheduled.setOnClickListener(listener)
                holder.filterInPlay.setOnClickListener(listener)
                holder.filterFinished.setOnClickListener(listener)
            }
            is MatchViewHolder -> {
                val offset = if (!showCarousel) 0 else (if (isSearchActive()) 1 else 2)
                val match = matches[position - offset]

                holder.tvPeriod.text = match.period
                holder.tvTeam1.text = match.team1
                holder.tvTeam2.text = match.team2
                holder.tvScore1.text = match.score1.toString()
                holder.tvScore2.text = match.score2.toString()
                holder.tvCompetition.text = match.competitionName ?: ""

                val loader = getImageLoader(holder.itemView.context)
                holder.ivCrest1.load(match.crest1, loader) {
                    crossfade(true)
                    placeholder(R.mipmap.ic_launcher_round)
                    error(R.mipmap.ic_launcher_round)
                }
                holder.ivCrest2.load(match.crest2, loader) {
                    crossfade(true)
                    placeholder(R.mipmap.ic_launcher_round)
                    error(R.mipmap.ic_launcher_round)
                }

                updateFavoriteIcon(holder.ivFavorite, match.isFavorite)
                holder.ivFavorite.setOnClickListener {
                    match.isFavorite = !match.isFavorite
                    updateFavoriteIcon(holder.ivFavorite, match.isFavorite)
                    onFavoriteChanged() // Уведомляем фрагмент для сохранения кэша
                }

                holder.itemView.setOnClickListener {
                    val fragment = SecondFragment()
                    val bundle = android.os.Bundle().apply {
                        putString("team1", match.team1)
                        putString("team2", match.team2)
                        putString("period", match.period)
                        putInt("score1", match.score1)
                        putInt("score2", match.score2)
                        putString("crest1", match.crest1)
                        putString("crest2", match.crest2)
                    }
                    fragment.arguments = bundle
                    val activity = holder.itemView.context as FragmentActivity
                    activity.supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .addToBackStack(null)
                        .commit()
                }
            }
        }
    }

    private fun updateFilterSelection(holder: FiltersViewHolder) {
        holder.filterAll.isSelected = currentStatus == "Все"
        holder.filterScheduled.isSelected = currentStatus == "Запланированы"
        holder.filterInPlay.isSelected = currentStatus == "Идут"
        holder.filterFinished.isSelected = currentStatus == "Завершены"
    }

    private fun updateFavoriteIcon(imageView: ImageView, isFavorite: Boolean) {
        imageView.setColorFilter(if (isFavorite) android.graphics.Color.parseColor("#FF9800") else android.graphics.Color.parseColor("#A2A2A2"))
    }

    override fun getItemCount(): Int {
        if (!showCarousel) return matches.size
        val extraItems = if (isSearchActive()) 1 else 2
        return matches.size + extraItems
    }

    fun filter(query: String? = null, status: String? = null): Int {
        if (query != null) currentQuery = query
        if (status != null) currentStatus = status

        val filteredList = matchesFull.filter { match ->
            val matchesQuery = currentQuery.isEmpty() || match.team1.contains(currentQuery, ignoreCase = true) || match.team2.contains(currentQuery, ignoreCase = true)
            val matchesStatus = when (currentStatus) {
                "Все" -> true
                "Запланированы" -> match.status == "SCHEDULED" || match.status == "TIMED"
                "Идут" -> match.status == "IN_PLAY" || match.status == "LIVE"
                "Завершены" -> match.status == "FINISHED"
                else -> true
            }
            matchesQuery && matchesStatus
        }
        matches = filteredList
        notifyDataSetChanged()
        onResultCountChanged(matches.size)
        return matches.size
    }

    fun updateData(newMatches: List<Match_info>) {
        matchesFull = ArrayList(newMatches)
        filter()
    }

    fun getAllData(): List<Match_info> = matchesFull
}
