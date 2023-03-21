package com.example.check_money

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.check_money.databinding.ActivityInputPopupBinding
import java.text.SimpleDateFormat
import java.util.*

class InputPopupActivity : AppCompatActivity(), View.OnClickListener {
    private val TAG = "InputPopupActivity"

    private lateinit var inputBinding: ActivityInputPopupBinding

    private var isThePaymentFlag: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //View Binding
        inputBinding = ActivityInputPopupBinding.inflate(layoutInflater)
        setContentView(inputBinding.root)

        //넘어온 모드 값
        isThePaymentFlag = intent.getBooleanExtra("POPUP_TYPE", true)    //납부 or 지출

        //Dialog 제목
        if(isThePaymentFlag) {
            inputBinding.textViewInputTitle.text = "납부"
            inputBinding.textViewInputContent.text = "납부자 :"
        }
        else {
            inputBinding.textViewInputTitle.text = "지출"
            inputBinding.textViewInputContent.text = "지출내용 :"
        }

        //Button Listener
        inputBinding.buttonInputOkay.setOnClickListener(this)       //확인 버튼 Listener
        inputBinding.buttonInputCancel.setOnClickListener(this)     //취소 버튼 Listener
        inputBinding.buttonInputDateChange.setOnClickListener(this) //날짜 변경 버튼 Listener

        inputBinding.textViewInputDate.text = getNowDate()          //날짜
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.button_input_okay -> {           //확인 버튼
                val intentOfOkay = Intent()
                intentOfOkay.putExtra("SELECTED_DATE", inputBinding.textViewInputDate.text)
                intentOfOkay.putExtra("AMOUNT", inputBinding.editTextInputAmount.text.toString().toInt())
                intentOfOkay.putExtra("CONTENT", inputBinding.editTextInputContent.text.toString())
                intentOfOkay.putExtra("POPUP_TYPE", isThePaymentFlag)

                setResult(RESULT_OK, intentOfOkay)
                finish()
            }
            R.id.button_input_cancel -> {         //취소 버튼
                setResult(RESULT_CANCELED)
                finish()
            }
            R.id.button_input_date_change -> {    //날짜 변경 버튼
                //CalendarDialog 띄우기
                var selectedDateArray = inputBinding.textViewInputDate.text.toString().split("-")
                val calendar = Calendar.getInstance()
                calendar.set(
                    selectedDateArray[0].toInt(),
                    selectedDateArray[1].toInt() - 1,
                    selectedDateArray[2].toInt())  //날짜 초기값을 현재 선택된 날짜로
                val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
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

                    inputBinding.textViewInputDate.text = dateStringBuilder.toString()
                }
                DatePickerDialog(
                    this,
                    dateSetListener,
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                ).show()
            }
        }
    }

    //현재 날짜 가져오기
    private fun getNowDate(): String {
        val longNow = System.currentTimeMillis()                //Long타입 현재 날짜 시간
        val dateNow = Date(longNow)                             //Long타입 -> Date타입
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")  //표시 형식

        return dateFormat.format(dateNow)
    }
}