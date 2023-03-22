package com.example.check_money

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore.Files
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
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.room.Room
import com.example.check_money.databinding.FragmentSettingBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.Cell
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.nio.file.attribute.FileOwnerAttributeView
import kotlin.system.exitProcess


class SettingFragment : Fragment(), View.OnClickListener {
    private val TAG: String = "SettingFragment"

    private lateinit var settingBinding: FragmentSettingBinding

    private val utils = Utils()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        settingBinding = FragmentSettingBinding.inflate(inflater, container, false)

        settingBinding.buttonSettingExport.text = "${MainActivity.bookName}을 엑셀로 내보내기"

        settingBinding.buttonSettingChangeTheBook.setOnClickListener(this)      //BookName 변경 버튼 Listener
        settingBinding.buttonSettingAddMeeting.setOnClickListener(this)         //Book 추가 버튼 Listener
        settingBinding.buttonSettingDeleteMeeting.setOnClickListener(this)      //Book 삭제 버튼 Listener
        settingBinding.buttonSettingExport.setOnClickListener(this)             //내보내기 버튼 Listener

        settingBinding.radioButtonSettingLightTheme.setOnClickListener(this)    //Light theme 설정 버튼
        settingBinding.radioButtonSettingDarkTheme.setOnClickListener(this)     //Dark theme 설정 버튼
        settingBinding.radioButtonSettingDefaultTheme.setOnClickListener(this)  //Default theme 설정 버튼

        createSpinner(MainActivity.bookName)

        //Theme 값에 따라 radio button 체크
        val sharedPreferences = this.activity?.getSharedPreferences(MainActivity.sharedFileName, AppCompatActivity.MODE_PRIVATE)
        when(sharedPreferences?.getInt("THEME", AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY)) {
            AppCompatDelegate.MODE_NIGHT_NO -> settingBinding.radioGroupSettingTheme.check(R.id.radio_button_setting_light_theme)
            AppCompatDelegate.MODE_NIGHT_YES -> settingBinding.radioGroupSettingTheme.check(R.id.radio_button_setting_dark_theme)
            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM, AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY -> settingBinding.radioGroupSettingTheme.check(R.id.radio_button_setting_default_theme)
        }

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
                            //SharedPreferences에 BookName 저장
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
            R.id.button_setting_export -> exportExcel(settingBinding.spinnerSettingBookName.selectedItem.toString(), MainActivity.makingABook)
            R.id.radio_button_setting_light_theme -> utils.changeTheme(requireContext(), AppCompatDelegate.MODE_NIGHT_NO)
            R.id.radio_button_setting_dark_theme -> utils.changeTheme(requireContext(), AppCompatDelegate.MODE_NIGHT_YES)
            R.id.radio_button_setting_default_theme -> utils.changeTheme(requireContext(), AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY)
        }
    }

    //Excel 파일로 외부저장소에 내보내기
    private fun exportExcel(groupName: String, book: ArrayList<AccountBook>) {
        val workbook = HSSFWorkbook()
        val sheet = workbook.createSheet(groupName)

        var i = 0
        var row = sheet.createRow(i++)
        var cell: Cell?

        var headStr = listOf("날짜", "구분", "금액", "내용")
        var j = 0
        for (head in headStr) {
            cell = row.createCell(j++)
            cell.setCellValue(head)
        }

        for (page in book) {
            row = sheet.createRow(i++)

            cell = row.createCell(0)
            cell.setCellValue(page.date)

            cell = row.createCell(1)
            cell.setCellValue(page.mode)

            cell = row.createCell(2)
            cell.setCellValue(page.amount.toString())

            cell = row.createCell(3)
            cell.setCellValue(page.content)
        }

        //Document에 gruopName.xls로 저장
        val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = File(dir, "$groupName.xls")

        //경로에 파일 있으면 삭제
        if(file.exists())
            file.delete()

        //Excel파일 저장
        try {
            workbook.write(file.outputStream())
            Toast.makeText(requireContext(), "$groupName.xls가 ${Environment.DIRECTORY_DOWNLOADS}에 저장 되었습니다.", Toast.LENGTH_SHORT).show()
        }catch(e: FileNotFoundException) {
            e.stackTrace
        }catch(e: IOException) {
            e.stackTrace
        }
    }

    //계 추가, 삭제 Dialog
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
                        if(!MainActivity.bookshelf.contains(inputStr)) {
                            MainActivity.bookshelf.add(inputStr)
                        }
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

                    createSpinner(inputStr)     //Spinner item 목록 재생성
            })
            .setNegativeButton("취소", DialogInterface.OnClickListener() {
                dialogInterface, _ ->
                    dialogInterface.dismiss()   //Dialog 닫기
                    Toast.makeText(requireContext(), "취소되었습니다.", Toast.LENGTH_SHORT).show()
            })
        addDialog.show()
    }

    //Spinner
    private fun createSpinner(keyword: String) {
        //Spinner item 목록 추가
        var setToListBookshelf = ArrayList<String>(MainActivity.bookshelf)              //HashSet -> ArrayList
        setToListBookshelf.sort()

        val bookshelfAdapter = ArrayAdapter(                                            //Spinner Adapter
            requireContext(),
            android.R.layout.simple_list_item_1,
            setToListBookshelf
        )

        settingBinding.spinnerSettingBookName.adapter = bookshelfAdapter
        settingBinding.spinnerSettingBookName.setSelection(findPosition(setToListBookshelf, keyword))
    }

    //계 삭제시 DB속 해당 계 삭제
    private fun deleteBook(bookName: String) {
        val db = Room.databaseBuilder(requireContext(), AppDatabase::class.java, "AccountBook").build()
        val accountBookDAO = db.accountBookDAO()
        CoroutineScope(Dispatchers.IO).launch {
            accountBookDAO.deleteBook(bookName)
        }
    }

    private fun findPosition(target: ArrayList<String>, keyword: String): Int {
        var position = 0
        for(i in 0 until target.size) {
            if(target[i] == keyword)
                position = i
        }

        return position
    }
}