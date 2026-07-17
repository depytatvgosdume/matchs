package com.example.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import android.widget.ImageButton
import coil.load
import coil.decode.SvgDecoder
import coil.ImageLoader

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
        
        val team1 = arguments?.getString("team1") ?: "Команда 1"
        val team2 = arguments?.getString("team2") ?: "Команда 2"
        val period = arguments?.getString("period") ?: "15.07 20:00"
        val score1 = arguments?.getInt("score1") ?: 0
        val score2 = arguments?.getInt("score2") ?: 0
        val crest1 = arguments?.getString("crest1")
        val crest2 = arguments?.getString("crest2")

        // Находим вьюхи
        val tvTeam1 = view.findViewById<TextView>(R.id.tv_team1_detail)
        val tvTeam2 = view.findViewById<TextView>(R.id.tv_team2_detail)
        val tvDate = view.findViewById<TextView>(R.id.tv_date)
        val tvScore = view.findViewById<TextView>(R.id.tv_score)
        val ivCrest1 = view.findViewById<ImageView>(R.id.iv_crest1_detail)
        val ivCrest2 = view.findViewById<ImageView>(R.id.iv_crest2_detail)
        val btnBack = view.findViewById<ImageButton>(R.id.btn_go_back)

        // Заполняем текстовые данные
        tvTeam1.text = team1
        tvTeam2.text = team2
        tvDate.text = period
        tvScore.text = "$score1 : $score2"

        // Настройка загрузчика Coil
        val loader = ImageLoader.Builder(requireContext())
            .components { add(SvgDecoder.Factory()) }
            .okHttpClient {
                okhttp3.OkHttpClient.Builder()
                    .hostnameVerifier { _, _ -> true }
                    .build()
            }
            .build()

        // Загрузка логотипов
        ivCrest1.load(crest1, loader) {
            crossfade(true)
            placeholder(R.mipmap.ic_launcher_round)
            error(R.mipmap.ic_launcher_round)
        }
        ivCrest2.load(crest2, loader) {
            crossfade(true)
            placeholder(R.mipmap.ic_launcher_round)
            error(R.mipmap.ic_launcher_round)
        }

        // Заполнение заглушек статистики
        setupDummyStats(view)

        // Кнопка назад
        btnBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    private fun setupDummyStats(view: View) {
        val possession = view.findViewById<View>(R.id.stat_possession)
        if (possession != null) {
            possession.findViewById<TextView>(R.id.tv_stat_label).text = "Владение %"
            possession.findViewById<TextView>(R.id.tv_stat_home).text = "54"
            possession.findViewById<TextView>(R.id.tv_stat_away).text = "46"
        }

        val shots = view.findViewById<View>(R.id.stat_shots)
        if (shots != null) {
            shots.findViewById<TextView>(R.id.tv_stat_label).text = "Удары"
            shots.findViewById<TextView>(R.id.tv_stat_home).text = "12"
            shots.findViewById<TextView>(R.id.tv_stat_away).text = "8"
        }

        val corners = view.findViewById<View>(R.id.stat_corners)
        if (corners != null) {
            corners.findViewById<TextView>(R.id.tv_stat_label).text = "Угловые"
            corners.findViewById<TextView>(R.id.tv_stat_home).text = "5"
            corners.findViewById<TextView>(R.id.tv_stat_away).text = "3"
        }
    }
}
