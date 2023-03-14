package com.example.check_money

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.check_money.databinding.FragmentSettingBinding


class SettingFragment : Fragment() {
    private val TAG: String = "SettingFragment"

    private lateinit var settingBinding: FragmentSettingBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        settingBinding = FragmentSettingBinding.inflate(inflater, container, false)

        settingBinding.buttonSettingSave.setOnClickListener {
            MainActivity.bookName = settingBinding.editTextSettingBookName.text.toString()
            Log.i(TAG, "bookName = " + MainActivity.bookName)
        }

//        val editor = sharedPreferences.edit()
//        editor.putString("BOOK_NAME", bookName)
//        editor.apply()
//
//        Log.i(TAG, "Out BOOK_NAME = $bookName")

        return settingBinding.root
    }
}