package com.example.check_money

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.check_money.databinding.ActivityContentOfThePagePopupBinding

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
        var utils = Utils()
        var contentOfThePage: AccountBook = utils.serializableObjectFormat(intent, "CONTENT_OF_THE_PAGE")!!

        seq = contentOfThePage.seq  //현재 선택된 기록의 seq값 저장

        contentOfThePageBinding.editTextContentOfThePageDate.setText(contentOfThePage.date)                 //날짜
        contentOfThePageBinding.editTextContentOfThePageAmount.setText(contentOfThePage.amount.toString())  //금액
        contentOfThePageBinding.editTextContentOfThePageContent.setText(contentOfThePage.content)           //내용

        contentOfThePageBinding.buttonContentOfThePageUpdate.setOnClickListener(this)                       //수정 버튼 Listener
        contentOfThePageBinding.buttonContentOfThePageDelete.setOnClickListener(this)                       //삭제 버튼 Listener
        contentOfThePageBinding.buttonContentOfThePageCancel.setOnClickListener(this)                       //취소 버튼 Listener
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.button_content_of_the_page_update -> {     //수정 버튼
                var intentOfUpdate = Intent()
                intentOfUpdate.putExtra("SEQ", seq)
                intentOfUpdate.putExtra("DATE", contentOfThePageBinding.editTextContentOfThePageDate.text.toString())
                intentOfUpdate.putExtra("AMOUNT", contentOfThePageBinding.editTextContentOfThePageAmount.text.toString().toInt())
                intentOfUpdate.putExtra("CONTENT", contentOfThePageBinding.editTextContentOfThePageContent.text.toString())

                setResult(RESULT_OK, intentOfUpdate)
                finish()
            }
            R.id.button_content_of_the_page_delete -> {     //삭제 버튼
                setResult(-2)
                finish()
            }
            R.id.button_content_of_the_page_cancel -> {     //취소 버튼
                setResult(RESULT_CANCELED)
                finish()
            }
        }
    }
}