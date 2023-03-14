package com.example.check_money

import android.content.Intent
import android.os.Build

class Utils {
    fun serializableArrayFormat(intent: Intent, keyword: String): ArrayList<AccountBook> =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            intent.getSerializableExtra(keyword, AccountBook::class.java) as ArrayList<AccountBook>
        else
            intent.getSerializableExtra(keyword) as ArrayList<AccountBook>

    fun serializableObjectFormat(intent: Intent, keyword: String): AccountBook =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            intent.getSerializableExtra(keyword, AccountBook::class.java)!!
        else
            intent.getSerializableExtra(keyword) as AccountBook
}