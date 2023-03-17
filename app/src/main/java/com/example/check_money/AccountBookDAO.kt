package com.example.check_money

import androidx.room.*

@Dao
interface AccountBookDAO {
    @Insert
    fun insertAccountBook(accountBook: AccountBook)

    @Delete
    fun deleteAccountBook(accountBook: AccountBook)

    @Update
    fun updateAccountBook(accountBook: AccountBook)

    //선택한 계모임 기록만 가져오기
    @Query("SELECT * FROM AccountBook WHERE bookName = :bookName")
    fun getAccountBook(bookName: String): List<AccountBook>

    //납부자 명단 가져오기
    @Query("SELECT DISTINCT content FROM AccountBook WHERE bookName = :bookName AND mode = :mode")
    fun getSingleContent(bookName: String, mode: String): List<String>

    //계모임 가져오기
    @Query("SELECT DISTINCT bookName FROM AccountBook")
    fun getBookshelf(): List<String>

    //계모임 삭제
    @Query("DELETE FROM AccountBook WHERE bookName = :bookName")
    fun deleteBook(bookName: String)

//    @Query("SELECT * FROM AccountBook")
//    fun selectAccountBook(): List<AccountBook>
}