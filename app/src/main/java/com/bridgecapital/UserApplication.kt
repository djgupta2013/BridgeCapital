package com.bridgecapital

import android.app.Application
import com.bridgecapital.db.database.MyRoomDatabase
import com.bridgecapital.repository.TransactionRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class UserApplication: Application() {
    val applicationScope = CoroutineScope(SupervisorJob())

    val database by lazy { MyRoomDatabase.getDatabase(this,applicationScope) }
    val repository by lazy { TransactionRepository(database.transactionDao()) }
}