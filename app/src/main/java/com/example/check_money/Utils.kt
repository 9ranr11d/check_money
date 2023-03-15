package com.example.check_money

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.viewbinding.ViewBinding
import java.util.*
import kotlin.collections.ArrayList

class Utils {
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
}