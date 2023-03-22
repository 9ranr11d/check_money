package com.example.check_money

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class AccountBook(
    @PrimaryKey val seq: Int,
    @ColumnInfo val bookName: String,
    @ColumnInfo val date: String,
    @ColumnInfo val mode: String,
    @ColumnInfo val amount: Int,
    @ColumnInfo val content: String
): Parcelable, Comparable<AccountBook> {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readString()!!
    ) {
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(p: Parcel, f: Int) {
        p.writeInt(seq)
        p.writeString(bookName)
        p.writeString(date)
        p.writeString(mode)
        p.writeInt(amount)
        p.writeString(content)
    }

    companion object CREATOR : Parcelable.Creator<AccountBook> {
        override fun createFromParcel(parcel: Parcel): AccountBook {
            return AccountBook(parcel)
        }

        override fun newArray(size: Int): Array<AccountBook?> {
            return arrayOfNulls(size)
        }
    }

    //목록을 날짜순으로 정렬
    override fun compareTo(other: AccountBook): Int {
        return this.date.compareTo(other.date)
    }
}
