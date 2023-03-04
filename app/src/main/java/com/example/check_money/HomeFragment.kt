package com.example.check_money

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.room.Room
import com.example.check_money.databinding.FragmentHomeBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.collections.ArrayList


class HomeFragment : Fragment(), View.OnClickListener {
    private val TAG: String = "HomeFragment"
    private lateinit var homeBinding: FragmentHomeBinding
    private lateinit var homeLauncher: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeBinding = FragmentHomeBinding.inflate(layoutInflater)   //View Binding

        updateBalance(MainActivity.makingABook)

        //Button Listener
        homeBinding.buttonHomePayment.setOnClickListener(this)      //납부 버튼 Listener
        homeBinding.buttonHomeDisburse.setOnClickListener(this)     //지출 버튼 Listener

        //다시 넘어온 값
        homeLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            when(it.resultCode) {
                Activity.RESULT_OK -> {
                    val receivedData: Intent? = it.data

                    var selectedDate = receivedData?.getStringExtra("SELECTED_DATE")
                    var amount = receivedData?.getIntExtra("AMOUNT", 0)
                    var content = receivedData?.getStringExtra("CONTENT")
                    var isThePaymentFlag = receivedData?.getBooleanExtra("POPUP_TYPE", true)

                    var mode: String = when(isThePaymentFlag) {
                        true -> "납부"
                        else -> "지출"
                    }

                    var pageToAdd = AccountBook(0, MainActivity.bookName, selectedDate!!, mode, amount!!, content)
                    MainActivity.makingABook.add(pageToAdd)

                    //Room에 데이터 추가
                    val db = Room.databaseBuilder(requireContext(), AppDatabase::class.java, "AccountBook").build()
                    val accountBookDAO = db.accountBookDAO()
                    CoroutineScope(Dispatchers.IO).launch {
                        accountBookDAO.insertAccountBook(pageToAdd)
                    }

                    updateBalance(MainActivity.makingABook)
                }
                Activity.RESULT_CANCELED -> Log.i(TAG, "Cancel")
            }
        }

        return homeBinding.root
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.button_home_payment -> {
                val intentOfPayment = Intent(context, InputPopupActivity::class.java)
                intentOfPayment.putExtra("POPUP_TYPE", true)

                homeLauncher.launch(intentOfPayment)
            }
            R.id.button_home_disburse -> {
                val intentOfDisburse = Intent(context, InputPopupActivity::class.java)
                intentOfDisburse.putExtra("POPUP_TYPE", false)

                homeLauncher.launch(intentOfDisburse)
            }
        }
    }

    private fun updateBalance(makingABook: ArrayList<AccountBook>) {
        var balance = 0     //잔액

        for(page in makingABook) {
            when (page.mode) {
                "납부" -> balance += page.amount  //잔액 +
                "지출" -> balance -= page.amount  //잔액 -
                else -> {                        //그외는 삭제
                    Log.e(TAG, "The rest page = $page")
                    TODO("삭제 로직")
                }
            }
        }

        homeBinding.textViewHomeBalance.text = balance.toString()   //잔액 표시
    }
}