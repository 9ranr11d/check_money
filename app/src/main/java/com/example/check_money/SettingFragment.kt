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
    private lateinit var bindingSetting: FragmentSettingBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bindingSetting = FragmentSettingBinding.inflate(inflater, container, false)

        bindingSetting.buttonSettingSave.setOnClickListener {
            MainActivity.bookName = bindingSetting.editTextSettingBookName.text.toString()
            Log.i(TAG, "bookName = " + MainActivity.bookName)
        }

        return bindingSetting.root
    }
}