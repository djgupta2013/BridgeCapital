package com.bridgecapital.repository

import com.bridgecapital.db.dao.TransactionDao
import com.bridgecapital.db.table.TransactionTable
import kotlinx.coroutines.flow.Flow

class TransactionRepository(private val transactionDao: TransactionDao) {
    val allTransaction: Flow<List<TransactionTable>> = transactionDao.getAllTransaction()

    val allAmount: Flow<Int> = transactionDao.getTotal()

    val allCashInAmount: Flow<Int>  = transactionDao.getCashInAmount()
    val allCashOutAmount: Flow<Int>  = transactionDao.getCashOutAmount()

    suspend fun insert(transactionTable: TransactionTable) {
        transactionDao.insertTransaction(transactionTable)
    }

    suspend fun dateCheck(date: String){
        transactionDao.dateCheck(date)
    }
}