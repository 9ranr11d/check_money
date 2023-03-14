package com.example.check_money

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AccountBook(
    @PrimaryKey val seq: Int,
    @ColumnInfo val bookName: String,
    @ColumnInfo val date: String,
    @ColumnInfo val mode: String,
    @ColumnInfo val amount: Int,
    @ColumnInfo val content: String
): java.io.Serializable, Comparable<AccountBook> {
    //목록을 날짜순으로 정렬
    override fun compareTo(other: AccountBook): Int {
        return this.date.compareTo(other.date)
    }

}
