package com.bridgecapital.viewModel

import androidx.lifecycle.*
import com.bridgecapital.db.table.TransactionTable
import com.bridgecapital.repository.TransactionRepository
import kotlinx.coroutines.launch

class TransactionViewModel(private val transactionRepository: TransactionRepository) : ViewModel() {
    val allTransaction: LiveData<List<TransactionTable>> =
        transactionRepository.allTransaction.asLiveData()

    val allAmount: LiveData<Int> = transactionRepository.allAmount.asLiveData()
    val allCashInAmount: LiveData<Int> = transactionRepository.allCashInAmount.asLiveData()
    val allCashOutAmount: LiveData<Int> = transactionRepository.allCashOutAmount.asLiveData()

    fun insert(transactionTable: TransactionTable) = viewModelScope.launch {
        transactionRepository.insert(transactionTable)
    }

    fun dateCheck(date: String) = viewModelScope.launch {
        transactionRepository.dateCheck(date)
    }
}

class TransactionViewModelFactory(private val transactionRepository: TransactionRepository):ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TransactionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TransactionViewModel(transactionRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}