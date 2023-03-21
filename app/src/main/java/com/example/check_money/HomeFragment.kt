package com.example.check_money

import android.app.Activity
import android.content.Intent
import android.database.sqlite.SQLiteConstraintException
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.room.Room
import com.example.check_money.databinding.FragmentHomeBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList


class HomeFragment : Fragment(), View.OnClickListener {
    private val TAG = "HomeFragment"

    private lateinit var homeBinding: FragmentHomeBinding

    private lateinit var homeLauncher: ActivityResultLauncher<Intent>

    private var listOfCheckedPeople: ArrayList<AccountBook> = ArrayList()   //납부자 명단
    private var listOfExpenditure: ArrayList<AccountBook> = ArrayList()     //지출 내역 목록

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //View Binding
        homeBinding = FragmentHomeBinding.inflate(layoutInflater)

        //Button Listener
        homeBinding.buttonHomePayment.setOnClickListener(this)              //납부 버튼 Listener
        homeBinding.buttonHomeExpenditure.setOnClickListener(this)          //지출 버튼 Listener
        homeBinding.buttonHomeCheckedPeopleBook.setOnClickListener(this)    //납부자 자세히 보기 버튼 Listener
        homeBinding.buttonHomeExpenditureBook.setOnClickListener(this)      //지출 내역 자세히 보기 버튼 Listener

        homeBinding.textViewHomeGroupName.text = MainActivity.bookName      //계 이름 표시

        balanceUpdate()   //잔액 표시

        //다시 넘어온 값
        homeLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            when(it.resultCode) {
                Activity.RESULT_OK -> {
                    val receivedData = it.data

                    var selectedDate = receivedData?.getStringExtra("SELECTED_DATE")
                    var amount = receivedData?.getIntExtra("AMOUNT", 0)
                    var content = receivedData?.getStringExtra("CONTENT")
                    var isThePaymentFlag = receivedData?.getBooleanExtra("POPUP_TYPE", true)

                    var mode: String = when(isThePaymentFlag) {
                        true -> "납부"
                        else -> "지출"
                    }

                    insertBook(MainActivity.seq++, MainActivity.bookName, selectedDate!!, mode, amount!!, content!!)
                }
                Activity.RESULT_CANCELED -> Toast.makeText(context, "취소되었습니다.", Toast.LENGTH_SHORT).show()
            }
            balanceUpdate()     //잔액 최신화
        }

        return homeBinding.root
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.button_home_payment -> {               //납부 버튼
                val intentOfPayment = Intent(context, InputPopupActivity::class.java)
                intentOfPayment.putExtra("POPUP_TYPE", true)

                homeLauncher.launch(intentOfPayment)
            }
            R.id.button_home_expenditure -> {           //지출 버튼
                val intentOfExpenditure = Intent(context, InputPopupActivity::class.java)
                intentOfExpenditure.putExtra("POPUP_TYPE", false)

                homeLauncher.launch(intentOfExpenditure)
            }
            R.id.button_home_checked_people_book -> {   //납부자 목록 자세히 보기
                val intentOfPageOfABook1 = Intent(context, PagesOfABookPopupActivity::class.java)
                intentOfPageOfABook1.putExtra("PAGES", listOfCheckedPeople)

                homeLauncher.launch(intentOfPageOfABook1)
            }
            R.id.button_home_expenditure_book -> {      //지출 내역 자세히 보기
                val intentOfPageOfABook2 = Intent(context, PagesOfABookPopupActivity::class.java)
                intentOfPageOfABook2.putExtra("PAGES", listOfExpenditure)

                homeLauncher.launch(intentOfPageOfABook2)
            }
        }
    }

    //Room에 데이터 추가
    private fun insertBook(seq: Int, bookName: String,date: String, mode: String, amount: Int, content: String) {
        val db = Room.databaseBuilder(requireContext(), AppDatabase::class.java, "AccountBook").build()
        val accountBookDAO = db.accountBookDAO()

        var pageToAdd = AccountBook(
            seq,
            bookName,
            date,
            mode,
            amount,
            content
        )

        CoroutineScope(Dispatchers.IO).launch {
            try {
                accountBookDAO.insertAccountBook(pageToAdd)         //Room에 데이터 추가
                Log.d(TAG, "Insert $pageToAdd")
            }catch(e: SQLiteConstraintException) {
                Log.e(TAG, "SQLiteConstraintException")
                MainActivity.seq += 5
                insertBook(MainActivity.seq++, bookName, date, mode, amount, content)
                Log.d(TAG, "Seq $seq -> ${MainActivity.seq}")
            }catch(e: java.lang.Exception) {
                e.stackTrace
            }
        }

        MainActivity.makingABook.add(pageToAdd)                     //메인 목록에 추가
        if (mode == "납부")
            MainActivity.setOfCheckedPeople.add(content)            //납부자 목록에 추가('납부' 일때만)
    }

    //목록의 금액 합계
    private fun sumOfAmountInList(list: ArrayList<AccountBook>): Int {
        var result = 0

        for(target in list)
            result += target.amount

        return result
    }

    //목록 정렬
    private fun sortTheList(target: ArrayList<AccountBook>) {
        Collections.sort(target, AccountBook::compareTo)
    }

    //잔액 계산
    private fun balanceUpdate() {
        divideOfList()
        var balance = 0     //잔액

        balance += sumOfAmountInList(listOfCheckedPeople)
        balance -= sumOfAmountInList(listOfExpenditure)

        homeBinding.textViewHomeBalance.text = balance.toString()   //잔액 표시
    }

    //목록을 '납부'와 '지출로 나눔
    private fun divideOfList() {
        listOfCheckedPeople.clear()
        listOfExpenditure.clear()

        for(page in MainActivity.makingABook) {
            when(page.mode) {
                "납부" -> listOfCheckedPeople.add(page)
                "지출" -> listOfExpenditure.add(page)
            }
        }

        showList()
    }

    //목록 띄우기
    private fun showList() {
        //목록 초기화
        homeBinding.gridLayoutHomeListOfCheckedPeople.removeAllViews()
        homeBinding.gridLayoutHomeListOfExpenditure.removeAllViews()

        sortTheList(listOfCheckedPeople)                    //납부자 목록을 날짜순으로 정렬
        sortTheList(listOfExpenditure)                      //지출 내역을 날짜순으로 정렬
        var setToListCheckedPeople = ArrayList<String>(MainActivity.setOfCheckedPeople)
        setToListCheckedPeople.sort()                      //납부자 명단을 이름순으로 정렬

        val utils = Utils()
        //납부한 사람 목록 띄우기
        for(person in setToListCheckedPeople) {
            val personName = TextView(context)                  //이름
            personName.text = person
            personName.width = utils.dpToPx(requireContext(), 50F)

            var dateOfLastRecord = ""

            for(target in listOfCheckedPeople) {
                if(target.content == person)
                    dateOfLastRecord = target.date
            }

            val dateOfLastRecordOfPerson = TextView(context)    //마지막 납부 날짜
            dateOfLastRecordOfPerson.text = dateOfLastRecord
            dateOfLastRecordOfPerson.width = utils.dpToPx(requireContext(), 100F)

            //목록에 추가
            homeBinding.gridLayoutHomeListOfCheckedPeople.addView(personName)
            homeBinding.gridLayoutHomeListOfCheckedPeople.addView(dateOfLastRecordOfPerson)
        }

        //지출 목록 띄우기
        for(expenditureRecord in listOfExpenditure) {
            val expenditureDetails = TextView(context)          //지출 내용
            expenditureDetails.text = expenditureRecord.content
            expenditureDetails.width = utils.dpToPx(requireContext(), 50F)

            val expenditureAmount = TextView(context)           //지출 금액
            expenditureAmount.text = "${expenditureRecord.amount}원"
            expenditureAmount.width = utils.dpToPx(requireContext(), 50F)

            val expenditureDate = TextView(context)             //지출 날짜
            expenditureDate.text = expenditureRecord.date
            expenditureDate.width = utils.dpToPx(requireContext(), 100F)

            homeBinding.gridLayoutHomeListOfExpenditure.addView(expenditureDetails)
            homeBinding.gridLayoutHomeListOfExpenditure.addView(expenditureAmount)
            homeBinding.gridLayoutHomeListOfExpenditure.addView(expenditureDate)
        }
    }
}