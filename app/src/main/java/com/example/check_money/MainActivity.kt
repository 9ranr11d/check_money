package com.example.check_money

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.room.Room
import com.example.check_money.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationBarView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private val TAG: String = "MainActivity"
    private val sharedFileName: String = "drawer"

    private lateinit var mainBinding: ActivityMainBinding

    companion object {
        var bookName: String = ""
        var makingABook: ArrayList<AccountBook> = ArrayList()
        var setOfCheckedPeople: ArrayList<String> = ArrayList()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //View Binding
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)

        //바텀 네비게이션
        supportFragmentManager.beginTransaction().add(R.id.frame_layout_main, HomeFragment()).commit()
        mainBinding.bottomNavigationMain.setOnItemSelectedListener(NavigationBarView.OnItemSelectedListener {
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

        //sharedPreferences에 저장된 변수 가져오기
        val sharedPreferences = getSharedPreferences(sharedFileName, MODE_PRIVATE)
        bookName = sharedPreferences.getString("BOOK_NAME", "First_Book")!!
        Log.i(TAG, "In BOOK_NAME = $bookName")

        //Room
        val db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "AccountBook").build()
        val accountBookDAO = db.accountBookDAO()
        CoroutineScope(Dispatchers.IO).launch {
            makingABook = accountBookDAO.getAccountBook(bookName) as ArrayList<AccountBook>         //bookName에 맞는 기록만 가져오기
            Log.i(TAG, "Selected book = $bookName")

            setOfCheckedPeople = accountBookDAO.getSingleContent(bookName, "납부") as ArrayList<String>  //납부자 명단 가져오기
            Log.i(TAG, "List of checked people = $setOfCheckedPeople")
        }
    }
}