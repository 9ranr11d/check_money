package com.example.check_money

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.room.Room
import com.example.check_money.databinding.FragmentSettingBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.system.exitProcess


class SettingFragment : Fragment(), View.OnClickListener {
    private val TAG: String = "SettingFragment"

    private lateinit var settingBinding: FragmentSettingBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        settingBinding = FragmentSettingBinding.inflate(inflater, container, false)

        createSpinner()

        settingBinding.buttonSettingChangeTheBook.setOnClickListener(this)  //BookName 변경 버튼 Listener
        settingBinding.buttonSettingAddMeeting.setOnClickListener(this)     //Book 추가 버튼 Listener
        settingBinding.buttonSettingDeleteMeeting.setOnClickListener(this)  //Book 삭제 버튼 Listener

        return settingBinding.root
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.button_setting_change_the_book -> {    //BookName 변경 버튼
                //다시 시작 하기전 확인 Dialog
                val restartDialog = AlertDialog.Builder(requireContext())
                restartDialog.setTitle("주의")
                    .setMessage("확인을 누르면 재시작 됩니다.")
                    .setPositiveButton("확인", DialogInterface.OnClickListener() {
                        _, _ ->
                            //SharedPreferences에 BookName 갱신
                            val sharedPreferences = this.activity?.getSharedPreferences(MainActivity.sharedFileName, AppCompatActivity.MODE_PRIVATE)
                            val editor = sharedPreferences!!.edit()
                            var selectedBookName = settingBinding.spinnerSettingBookName.selectedItem.toString()
                            editor!!.putString("BOOK_NAME", selectedBookName)
                            editor.commit()

                            Log.i(TAG, "Out BOOK_NAME = $selectedBookName")

                            //앱 재시작
                            ActivityCompat.finishAffinity(requireActivity())
                            System.runFinalization()

                            val intentOfRestart = Intent(requireContext(), MainActivity::class.java)
                            startActivity(intentOfRestart)
                            exitProcess(0)
                    })
                    .setNegativeButton("취소", DialogInterface.OnClickListener() {
                        dialogInterface, _ ->
                            dialogInterface.dismiss()   //Dialog 닫기
                            Toast.makeText(requireContext(), "취소되었습니다.", Toast.LENGTH_SHORT).show()
                    })
                restartDialog.show()
            }
            R.id.button_setting_add_meeting -> {        //Book 추가 버튼
                createEditDialog("계 추가", true)
            }
            R.id.button_setting_delete_meeting -> {     //Book 삭제 버튼
                createEditDialog("계 삭제", false)
            }
        }
    }

    private fun createEditDialog(title: String, isAdd: Boolean) {
        var inputEdit = EditText(context)
        inputEdit.hint = "계 이름을 입력해 주세요"

        var positiveStr =
            if(isAdd)
                "추가"
            else
                "삭제"

        val addDialog = AlertDialog.Builder(requireContext())
        addDialog.setTitle(title)
            .setView(inputEdit)
            .setCancelable(false)
            .setPositiveButton(positiveStr, DialogInterface.OnClickListener() {
                _, _ ->
                    var inputStr = inputEdit.text.toString()
                    if(isAdd)
                        if(!MainActivity.bookshelf.contains(inputStr))
                            MainActivity.bookshelf.add(inputStr)
                        else
                            Toast.makeText(requireContext(), "이미 있는 계 모임입니다.", Toast.LENGTH_SHORT).show()
                    else {
                        if(MainActivity.bookshelf.contains(inputStr)) {
                            deleteBook(inputStr)
                            MainActivity.bookshelf.remove(inputStr)
                        }
                        else
                            Toast.makeText(requireContext(), "입력한 계 모임이 없습니다.", Toast.LENGTH_SHORT).show()
                    }

                createSpinner()     //Spinner item 목록 재생성
            })
            .setNegativeButton("취소", DialogInterface.OnClickListener() {
                dialogInterface, _ ->
                    dialogInterface.dismiss()   //Dialog 닫기
                    Toast.makeText(requireContext(), "취소되었습니다.", Toast.LENGTH_SHORT).show()
            })
        addDialog.show()
    }

    private fun createSpinner() {
        //Spinner item 목록 추가
        var setToListBookshelf = ArrayList<String>(MainActivity.bookshelf)              //HashSet -> ArrayList
        val bookshelfAdapter: ArrayAdapter<String> = ArrayAdapter(                      //Spinner Adapter
            requireContext(),
            android.R.layout.simple_list_item_1,
            setToListBookshelf
        )
        settingBinding.spinnerSettingBookName.adapter = bookshelfAdapter
    }

    private fun deleteBook(bookName: String) {
        val db = Room.databaseBuilder(requireContext(), AppDatabase::class.java, "AccountBook").build()
        val accountBookDAO = db.accountBookDAO()
        CoroutineScope(Dispatchers.IO).launch {
            accountBookDAO.deleteBook(bookName)
        }
    }
}