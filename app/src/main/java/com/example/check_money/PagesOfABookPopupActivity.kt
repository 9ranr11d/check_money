package com.example.check_money

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.room.Room
import com.example.check_money.databinding.ActivityPagesOfABookPopupBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PagesOfABookPopupActivity : AppCompatActivity(), View.OnClickListener {
    private val TAG = "PagesOfABookActivity"

    private lateinit var pageOfABookBinding: ActivityPagesOfABookPopupBinding

    private lateinit var pageOfABookLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //View Binding
        pageOfABookBinding = ActivityPagesOfABookPopupBinding.inflate(layoutInflater)
        setContentView(pageOfABookBinding.root)

        //Room
        val db = Room.databaseBuilder(application, AppDatabase::class.java, "AccountBook").build()
        val accountBookDAO = db.accountBookDAO()

        //Button Listener
        pageOfABookBinding.buttonPagesOfABookClose.setOnClickListener(this)

        //넘어 온 ArrayList<AccountBook>
        val utils = Utils()
        var pages = utils.serializableArrayFormat(intent, "PAGES")

        //Recycler View
        var pageAdapter = PageAdapter(pages)
        pageOfABookBinding.recyclerViewPageOfABook.adapter = pageAdapter

        lateinit var pageClicked: AccountBook   //선택한 목록 객체
        var pageNumber = 0                      //선택한 목록의 순서

        //RecyclerView 목록 선택 시 자세히 보기
        pageAdapter.setItemClickListener(object: PageAdapter.OnItemClickListener{
            override fun onClick(target: AccountBook, position: Int) {
                Log.i(TAG, "Clicked (Target = $target, Position = $position")
                pageClicked = target
                pageNumber = position

                var intentForPageClicks = Intent(applicationContext, ContentOfThePagePopupActivity::class.java)
                intentForPageClicks.putExtra("CONTENT_OF_THE_PAGE", target)

                pageOfABookLauncher.launch(intentForPageClicks)
            }
        })

        pageOfABookLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            when(it.resultCode) {
                Activity.RESULT_OK -> {     //수정 버튼
                    var receivedData = it.data
                    var selectedSeq = receivedData?.getIntExtra("SEQ", 0)
                    var selectedDate = receivedData?.getStringExtra("DATE")
                    var selectedAmount = receivedData?.getIntExtra("AMOUNT", 0)
                    var selectedContent = receivedData?.getStringExtra("CONTENT")

                    var updatePage = AccountBook(
                        selectedSeq!!,
                        pageClicked.bookName,
                        selectedDate!!,
                        pageClicked.mode,
                        selectedAmount!!,
                        selectedContent!!
                    )
                    //Room에 데이터 변경
                    CoroutineScope(Dispatchers.IO).launch {
                        accountBookDAO.updateAccountBook(updatePage)
                    }

                    pageAdapter.routineOfChanging(pageNumber, updatePage)
                }
                -2 -> {                     //삭제 버튼
                    //Room에서 데이터 삭제
                    CoroutineScope(Dispatchers.IO).launch {
                        accountBookDAO.deleteAccountBook(pageClicked)
                    }

                    pageAdapter.routineOfRemove(pageNumber)
                }
                Activity.RESULT_CANCELED -> Toast.makeText(this, "취소되었습니다.", Toast.LENGTH_SHORT).show() //취소 버튼
            }
        }
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.button_pages_of_a_book_close -> {
                setResult(RESULT_CANCELED)
                finish()
            }
        }
    }
}