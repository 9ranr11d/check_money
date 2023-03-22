package com.example.check_money

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.check_money.databinding.ActivityContentOfThePagePopupBinding
import java.util.*

class ContentOfThePagePopupActivity : AppCompatActivity(), View.OnClickListener {
    private val TAG = "ContentOfThePagePopupActivity"

    private lateinit var contentOfThePageBinding: ActivityContentOfThePagePopupBinding

    private var seq = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //View Binding
        contentOfThePageBinding = ActivityContentOfThePagePopupBinding.inflate(layoutInflater)
        setContentView(contentOfThePageBinding.root)

        //넘어온 Account객체
        var contentOfThePage =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                intent.getParcelableExtra("CONTENT_OF_THE_PAGE", AccountBook::class.java)
            else
                intent.getParcelableExtra("CONTENT_OF_THE_PAGE")


        seq = contentOfThePage!!.seq  //현재 선택된 기록의 seq값 저장

        if(contentOfThePage.mode == "납부")
            contentOfThePageBinding.textViewContentOfThePageContent.text = "납부자명 :"
        else
            contentOfThePageBinding.textViewContentOfThePageContent.text = "지출내용 :"

        contentOfThePageBinding.textViewContentOfThePageDate.text = contentOfThePage.date                   //날짜
        contentOfThePageBinding.editTextContentOfThePageAmount.setText(contentOfThePage.amount.toString())  //금액
        contentOfThePageBinding.editTextContentOfThePageContent.setText(contentOfThePage.content)           //내용

        contentOfThePageBinding.buttonContentOfThePageDateChange.setOnClickListener(this)                   //날짜 수정 버튼 Listener
        contentOfThePageBinding.buttonContentOfThePageUpdate.setOnClickListener(this)                       //수정 버튼 Listener
        contentOfThePageBinding.buttonContentOfThePageDelete.setOnClickListener(this)                       //삭제 버튼 Listener
        contentOfThePageBinding.buttonContentOfThePageCancel.setOnClickListener(this)                       //취소 버튼 Listener
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.button_content_of_the_page_update -> {         //수정 버튼
                var intentOfUpdate = Intent()
                intentOfUpdate.putExtra("SEQ", seq)
                intentOfUpdate.putExtra("DATE", contentOfThePageBinding.textViewContentOfThePageDate.text.toString())
                intentOfUpdate.putExtra("AMOUNT", contentOfThePageBinding.editTextContentOfThePageAmount.text.toString().toInt())
                intentOfUpdate.putExtra("CONTENT", contentOfThePageBinding.editTextContentOfThePageContent.text.toString())

                setResult(RESULT_OK, intentOfUpdate)
                finish()
            }
            R.id.button_content_of_the_page_delete -> {         //삭제 버튼
                setResult(-2)
                finish()
            }
            R.id.button_content_of_the_page_cancel -> {         //취소 버튼
                setResult(RESULT_CANCELED)
                finish()
            }
            R.id.button_content_of_the_page_date_change -> {    //날짜 변경 버튼
                //CalendarDialog 띄우기
                var selectedDateArray = contentOfThePageBinding.textViewContentOfThePageDate.text.toString().split("-")
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

                    contentOfThePageBinding.textViewContentOfThePageDate.text = dateStringBuilder.toString()
                }
                DatePickerDialog(
                    this,
                    R.style.Theme_Check_money_Calendar,
                    dateSetListener,
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                ).show()
            }
        }
    }
}