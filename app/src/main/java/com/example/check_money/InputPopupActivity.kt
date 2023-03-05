package com.example.check_money

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.check_money.databinding.ActivityInputPopupBinding
import java.text.SimpleDateFormat
import java.util.*

class InputPopupActivity : AppCompatActivity(), View.OnClickListener {
    private val TAG = "InputPopupActivity"
    private lateinit var inputPopupBinding: ActivityInputPopupBinding
    private var isThePaymentFlag: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //View Binding
        inputPopupBinding = ActivityInputPopupBinding.inflate(layoutInflater)
        setContentView(inputPopupBinding.root)

        //넘어온 값
        val receiveIntent = intent
        isThePaymentFlag = receiveIntent.getBooleanExtra("POPUP_TYPE", true)    //납부 or 지출

        //Dialog 제목
        if(isThePaymentFlag)
            inputPopupBinding.textViewInputPopupTitle.text = "납부"
        else
            inputPopupBinding.textViewInputPopupTitle.text = "지출"

        //Button Listener
        inputPopupBinding.buttonInputPopupOkay.setOnClickListener(this)     //확인 버튼 Listener
        inputPopupBinding.buttonInputPopupCancel.setOnClickListener(this)   //취소 버튼 Listener
        inputPopupBinding.buttonInputPopupDateChange.setOnClickListener(this)

        inputPopupBinding.textViewInputPopupDate.text = getNowDate()        //날짜
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.button_input_popup_okay -> {           //확인 버튼
                val intentOfOkay = Intent()
                intentOfOkay.putExtra("SELECTED_DATE", inputPopupBinding.textViewInputPopupDate.text)
                intentOfOkay.putExtra("AMOUNT", inputPopupBinding.editTextInputPopupAmount.text.toString().toInt())
                intentOfOkay.putExtra("CONTENT", inputPopupBinding.editTextInputPopupContent.text.toString())
                intentOfOkay.putExtra("POPUP_TYPE", isThePaymentFlag)

                setResult(RESULT_OK, intentOfOkay)
                finish()
            }
            R.id.button_input_popup_cancel -> {         //취소 버튼
                setResult(RESULT_CANCELED)
                finish()
            }
            R.id.button_input_popup_date_change -> {    //날짜 변경 버튼

                //CalendarDialog 띄우기
                var selectedDateArray = inputPopupBinding.textViewInputPopupDate.text.toString().split("-")
                val calendar = Calendar.getInstance()
                calendar.set(selectedDateArray[0].toInt(), selectedDateArray[1].toInt() - 1, selectedDateArray[2].toInt())  //날짜 초기값을 현재 선택된 날짜로
                val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                    var dateStringBuilder = StringBuilder()
                    dateStringBuilder.append(year).append("-")

                    //10이하로는 빈자리에 0이 들어가게
                    if(month < 9)
                        dateStringBuilder.append("0").append(month+1)
                    else
                        dateStringBuilder.append(month+1)

                    dateStringBuilder.append("-")

                    if(dayOfMonth < 10)
                        dateStringBuilder.append("0").append(dayOfMonth)
                    else
                        dateStringBuilder.append(dayOfMonth)

                    inputPopupBinding.textViewInputPopupDate.text = dateStringBuilder.toString()
                }
                DatePickerDialog(this, dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
            }
        }
    }

    //현재 날짜 가져오기
    fun getNowDate(): String {
        val longNow = System.currentTimeMillis()                //Long타입 현재 날짜 시간
        val dateNow = Date(longNow)                             //Long타입 -> Date타입
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")  //표시 형식

        return dateFormat.format(dateNow)
    }
}