package com.example.check_money

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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
        //Room
        val db = Room.databaseBuilder(requireContext(), AppDatabase::class.java, "AccountBook").build()
        val accountBookDAO = db.accountBookDAO()

        //Button Listener
        homeBinding.buttonHomePayment.setOnClickListener(this)      //납부 버튼 Listener
        homeBinding.buttonHomeDisburse.setOnClickListener(this)     //지출 버튼 Listener

        var listOfCheckedPeople: ArrayList<AccountBook> = ArrayList()
        var listOfDisburse: ArrayList<AccountBook> = ArrayList()

        for(page in MainActivity.makingABook) {
            when(page.mode) {
                "납부" -> listOfCheckedPeople.add(page)
                "지출" -> listOfDisburse.add(page)
            }
        }

        balanceUpdate(listOfCheckedPeople, listOfDisburse)          //잔액 표시

        //납부한 사람 목록 띄우기
        for(person in MainActivity.setOfCheckedPeople) {
            val personName = TextView(context)                  //이름
            personName.text = person

            var dateOfLastRecord = ""

            for(target in listOfCheckedPeople) {
                if(target.content == person)
                    dateOfLastRecord = target.date
            }

            val dateOfLastRecordOfPerson = TextView(context)    //마지막 납부 날짜
            dateOfLastRecordOfPerson.text = dateOfLastRecord

            homeBinding.gridLayoutHomeListOfCheckedPeople.addView(personName)
            homeBinding.gridLayoutHomeListOfCheckedPeople.addView(dateOfLastRecordOfPerson)
        }

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

                    var pageToAdd = AccountBook(0, MainActivity.bookName, selectedDate!!, mode, amount!!, content!!)
                    MainActivity.makingABook.add(pageToAdd)

                    Log.i(TAG, "Add = $pageToAdd")

                    //Room에 데이터 추가
                    CoroutineScope(Dispatchers.IO).launch {
                        accountBookDAO.insertAccountBook(pageToAdd)
                    }

                    balanceUpdate(listOfCheckedPeople, listOfDisburse)     //잔액 최신화
                }
                Activity.RESULT_CANCELED -> Log.i(TAG, "Cancel")
            }
        }

        return homeBinding.root
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.button_home_payment -> {   //납부 버튼
                val intentOfPayment = Intent(context, InputPopupActivity::class.java)
                intentOfPayment.putExtra("POPUP_TYPE", true)

                homeLauncher.launch(intentOfPayment)
            }
            R.id.button_home_disburse -> {  //지출 버튼
                val intentOfDisburse = Intent(context, InputPopupActivity::class.java)
                intentOfDisburse.putExtra("POPUP_TYPE", false)

                homeLauncher.launch(intentOfDisburse)
            }
        }
    }

    //잔액 계산
    private fun balanceUpdate(payment: ArrayList<AccountBook>, disburese: ArrayList<AccountBook>) {
        var balance = 0     //잔액

        balance += sumOfList(payment)
        balance -= sumOfList(disburese)

        homeBinding.textViewHomeBalance.text = balance.toString()   //잔액 표시
    }

    private fun sumOfList(list: ArrayList<AccountBook>): Int {
        var result = 0

        for(target in list)
            result += target.amount

        return result
    }
}