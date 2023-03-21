package com.example.check_money

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import android.util.TypedValue
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.viewbinding.ViewBinding
import java.util.*
import kotlin.collections.ArrayList
import kotlin.system.exitProcess

class Utils {
    private val TAG = "Utils"
    //ArrayList 직렬화 함수
    fun serializableArrayFormat(intent: Intent, keyword: String): ArrayList<AccountBook> =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            intent.getSerializableExtra(keyword, AccountBook::class.java) as ArrayList<AccountBook>
        else
            intent.getSerializableExtra(keyword) as ArrayList<AccountBook>

    //AccountBook 객체 직렬화 함수
    fun serializableObjectFormat(intent: Intent, keyword: String): AccountBook =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            intent.getSerializableExtra(keyword, AccountBook::class.java)!!
        else
            intent.getSerializableExtra(keyword) as AccountBook

    fun dpToPx(context: Context, dp: Float): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.resources.displayMetrics).toInt()
    }

    //Theme 설정
    fun changeTheme(context: Context, theme: Int) {
        val sharedPreferences = context.getSharedPreferences(MainActivity.sharedFileName, AppCompatActivity.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt("THEME", theme)
        editor.apply()

        Log.i(TAG, "Out Theme = $theme")

        when(theme) {
            AppCompatDelegate.MODE_NIGHT_NO -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            AppCompatDelegate.MODE_NIGHT_YES -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
            AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM -> {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                else
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY)
            }
        }
    }
}