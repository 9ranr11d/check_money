package com.example.check_money

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.check_money.databinding.FragmentHomeBinding


class HomeFragment : Fragment() {
    private lateinit var bindingHome: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bindingHome = FragmentHomeBinding.inflate(layoutInflater)
        return bindingHome.root
    }
}