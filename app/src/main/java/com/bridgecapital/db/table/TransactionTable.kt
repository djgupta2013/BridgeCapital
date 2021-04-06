package com.bridgecapital.db.table

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transaction_table")
class TransactionTable(
    @ColumnInfo(name = "party_name") val party_name: String,
    @ColumnInfo(name = "party_number") val party_number: String,
    @ColumnInfo(name = "amount") val amount: Int,
    @ColumnInfo(name = "full_amount_received") val full_amount_received: Boolean,
    @ColumnInfo(name = "paid_amount") val paid_amount: Int,
    @ColumnInfo(name = "pending_amount") val pending_amount: Int,
    @ColumnInfo(name = "image_bitmap") val image_bitmap: String,
    @ColumnInfo(name = "transaction_type") val transaction_type: String,
    @ColumnInfo(name = "current_date") val current_date: String,


) {
    @PrimaryKey(autoGenerate = true)
    var id = 0
}