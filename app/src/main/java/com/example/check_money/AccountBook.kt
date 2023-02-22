package com.example.check_money

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity
data class AccountBook(
    @PrimaryKey val seq: Int,
    @ColumnInfo val bookName: String?,
    @ColumnInfo val date: String?,
    @ColumnInfo val mode: String?,
    @ColumnInfo val amount: Int?,
    @ColumnInfo val content: String?
)
