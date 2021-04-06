package com.bridgecapital.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bridgecapital.db.table.TransactionTable
import kotlinx.coroutines.flow.Flow


@Dao
interface TransactionDao {

    @Query("SELECT * FROM TRANSACTION_TABLE ORDER BY id DESC")
    fun getAllTransaction(): Flow<List<TransactionTable>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTransaction(transactionTable: TransactionTable)

    @Query("DELETE FROM transaction_table")
    suspend fun deleteAllTransaction()

    @Query("SELECT SUM(amount) AS total FROM TRANSACTION_TABLE")
    fun getTotal(): Flow<Int>

    @Query("SELECT SUM(paid_amount) FROM TRANSACTION_TABLE WHERE transaction_type IN ('Income')")
    fun getCashInAmount(): Flow<Int>

    @Query("SELECT SUM(paid_amount) FROM TRANSACTION_TABLE WHERE transaction_type IN ('Expense')")
    fun getCashOutAmount(): Flow<Int>

    @Query("DELETE FROM TRANSACTION_TABLE WHERE `current_date` !=:date ")
    suspend fun dateCheck(date: String)
}