package com.example.check_money

import android.content.SharedPreferences.Editor
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
    private val TAG = "MainActivity"

    private lateinit var mainBinding: ActivityMainBinding

    private var allAccountBook = ArrayList<AccountBook>()

    companion object {
        const val sharedFileName = "Bookshelf"
        var bookName = ""
        var seq = 0

        var makingABook = ArrayList<AccountBook>()
        var setOfCheckedPeople = HashSet<String>()
        var bookshelf = HashSet<String>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //View Binding
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)

        createBottomNavigation()

        getSharedPreferences()
        getDataBase()
    }

    //Shared Preferences에 저장된 변수 가져오기
    private fun getSharedPreferences() {
        val sharedPreferences = getSharedPreferences(sharedFileName, MODE_PRIVATE)
        bookName = sharedPreferences.getString("BOOK_NAME", "First_Book")!!
        Log.i(TAG, "In BOOK_NAME = $bookName")

        seq = sharedPreferences.getInt("SEQ", 0)
        Log.i(TAG, "In SEQ = $seq")
    }

    //Room에서 데이터 가져오기
    private fun getDataBase() {
        val db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "AccountBook").build()
        val accountBookDAO = db.accountBookDAO()
        CoroutineScope(Dispatchers.IO).launch {
            makingABook = accountBookDAO.getAccountBook(bookName) as ArrayList<AccountBook>             //bookName에 해당하는 기록 가져오기
            Log.i(TAG, "Selected book = $bookName")

            setOfCheckedPeople = accountBookDAO.getSingleContent(bookName, "납부").toHashSet()    //납부자 명단 가져오기
            Log.i(TAG, "List of checked people = $setOfCheckedPeople")

            bookshelf += accountBookDAO.getBookshelf().toHashSet()
            Log.i(TAG, "Bookshelf = $bookshelf")

            //저장된 데이터가 없을 때
            if(bookshelf.isEmpty())
                bookshelf.add(bookName)
        }
    }

    //Bottom Navigation
    private fun createBottomNavigation() {
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
    }

    //종료시 SEQ값 저장
    override fun onStop() {
        super.onStop()
        val sharedPreferences = getSharedPreferences(sharedFileName, MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt("SEQ", seq)
        editor.apply()

        Log.i(TAG, "Out SEQ = $seq")
    }
}