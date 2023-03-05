package com.example.check_money

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [AccountBook::class],
    version = 1,
    exportSchema = true)
abstract class AppDatabase : RoomDatabase() {
    abstract fun accountBookDAO(): AccountBookDAO
}