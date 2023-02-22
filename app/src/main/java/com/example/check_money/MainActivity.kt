package com.example.check_money

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.check_money.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationBarView

class MainActivity : AppCompatActivity() {
    private lateinit var bindingMain: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingMain = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bindingMain.root)

        supportFragmentManager.beginTransaction().add(R.id.frame_layout_main, HomeFragment()).commit()
        bindingMain.bottomNavigationMain.setOnItemSelectedListener(NavigationBarView.OnItemSelectedListener {
            when(it.itemId) {
                R.id.item_navigation_home -> {
                    supportFragmentManager.beginTransaction().replace(R.id.frame_layout_main, HomeFragment()).commit()
                }
                R.id.item_navigation_setting -> {
                    supportFragmentManager.beginTransaction().replace(R.id.frame_layout_main, SettingFragment()).commit()
                }
            }
            true
        })
    }
}