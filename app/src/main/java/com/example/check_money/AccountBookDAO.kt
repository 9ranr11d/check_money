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

    @Query("SELECT * FROM AccountBook WHERE bookName = :bookName")
    fun getAccountBook(bookName: String): List<AccountBook>

    @Query("SELECT DISTINCT content FROM AccountBook WHERE bookName = :bookName AND mode = :mode")
    fun getSingleContent(bookName: String, mode: String): List<String>

    @Query("SELECT * FROM AccountBook WHERE mode = :mode")
    fun getListByMode(mode: String): List<AccountBook>
}