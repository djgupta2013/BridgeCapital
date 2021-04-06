package com.bridgecapital.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bridgecapital.R
import com.bridgecapital.UserApplication
import com.bridgecapital.adapter.TransactionAdapter
import com.bridgecapital.viewModel.TransactionViewModel
import com.bridgecapital.viewModel.TransactionViewModelFactory
import kotlinx.android.synthetic.main.activity_transaction_list.*

class TransactionListActivity : AppCompatActivity() {
    private val transactionViewModel: TransactionViewModel by viewModels {
        TransactionViewModelFactory((application as UserApplication).repository)
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction_list)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = getString(R.string.all_transaction_for_the_day)
        toolbar.navigationIcon = this.getDrawable(R.drawable.ic_arrow_back_24)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        val adapter = TransactionAdapter(this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        transactionViewModel.allTransaction.observe(this) { transaction ->
            try {
                transaction?.let {
                    adapter.submitList(it)
                }
                if (transaction.isEmpty()) {
                    tvDataNotFound.visibility = View.VISIBLE
                }
            }catch (e: Exception){
                e.printStackTrace()
            }

        }
        transactionViewModel.allAmount.observe(this) {
            it?.apply {
                Log.e("total", it.toString())
                tvTotalAmount.text = "${getString(R.string.total_amount)} $it"
                tvTotalAmount.visibility = View.VISIBLE
            }
        }
        transactionViewModel.allCashInAmount.observe(this) {
            it?.apply {
                Log.e("total Income>>> ", it.toString())
                tvCashIn.text = "${getString(R.string.total_cash)} $it"
                tvCashIn.visibility = View.VISIBLE
            }
        }
        transactionViewModel.allCashOutAmount.observe(this) {
            it?.apply {
                Log.e("total Expense>>> ", it.toString())
                tvCashOut.text = "${getString(R.string.total_expense)} $it"
                tvCashOut.visibility = View.VISIBLE
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}