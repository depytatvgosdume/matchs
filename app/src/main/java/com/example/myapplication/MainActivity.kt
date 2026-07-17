package com.example.myapplication

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

class MainActivity : AppCompatActivity() {

    private lateinit var btnHome: View
    private lateinit var btnSettings: View
    private lateinit var btnAccount: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnHome = findViewById(R.id.btn_home)
        btnSettings = findViewById(R.id.btn_settings)
        btnAccount = findViewById(R.id.btn_account)

        // Обработка кликов нижней панели
        btnHome.setOnClickListener {
            switchFragment(FirstFragment(), it)
        }

        btnSettings.setOnClickListener {
            switchFragment(SettingsFragment(), it)
        }

        btnAccount.setOnClickListener {
            switchFragment(AccountFragment(), it)
        }

        // При первом запуске показываем первый фрагмент
        if (savedInstanceState == null) {
            switchFragment(FirstFragment(), btnHome)
        }
    }

    private fun switchFragment(fragment: Fragment, selectedView: View) {
        // Подсветка кнопок
        btnHome.isSelected = (selectedView == btnHome)
        btnSettings.isSelected = (selectedView == btnSettings)
        btnAccount.isSelected = (selectedView == btnAccount)

        // Замена фрагмента
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}
