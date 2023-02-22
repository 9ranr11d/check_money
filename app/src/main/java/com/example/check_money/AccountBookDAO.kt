package com.example.check_money

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface AccountBookDAO {
    @Query("SELECT * FROM AccountBook WHERE bookName = ':bookName'")
    fun getBook(bookName: String): List<AccountBook>

    @Insert
    fun insertAll(accountBook: AccountBook)

    @Delete
    fun delete(accountBook: AccountBook)
}