package com.bridgecapital.adapter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.room.TypeConverter
import com.bridgecapital.R
import com.bridgecapital.db.table.TransactionTable
import de.hdodenhof.circleimageview.CircleImageView

class TransactionAdapter(private val context: Context) :
    ListAdapter<TransactionTable, TransactionAdapter.TransactionViewHolder>(
        TransactionComparator()
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        return TransactionViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val model = getItem(position)
        model.apply {
            holder.apply {
                tvPartyName.text = party_name
                tvPartyNumber.text = party_number
                tvAmount.text = amount.toString()
                if (!model.full_amount_received) {
                    tvPaidAmount.text = paid_amount.toString()
                    llPaid.visibility = View.VISIBLE
                    val pendingAmount = (amount - paid_amount).toString()
                    tvPendingAmount.text = pendingAmount
                }
                tvAllPayment.text = if (full_amount_received) {
                    "Yes"
                } else {
                    "No"
                }
                tvPaymentType.text = transaction_type
                try {
                    image.setImageBitmap(stringToBitMap(image_bitmap))
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    @TypeConverter
    fun stringToBitMap(encodedString: String?): Bitmap? {
        return try {
            val encodeByte: ByteArray = Base64.decode(encodedString, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.size)
            bitmap
        } catch (e: java.lang.Exception) {
            e.message
            null
        }
    }

    class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: CircleImageView = itemView.findViewById(R.id.ivDocument)
        val tvPartyName: TextView = itemView.findViewById(R.id.tvPartyName)
        val tvPartyNumber: TextView = itemView.findViewById(R.id.tvPartyNumber)
        val tvAmount: TextView = itemView.findViewById(R.id.tvAmount)
        val tvAllPayment: TextView = itemView.findViewById(R.id.tvAllPayment)
        val tvPaidAmount: TextView = itemView.findViewById(R.id.tvPaidAmount)
        val tvPendingAmount: TextView = itemView.findViewById(R.id.tvPendingAmount)
        val tvPaymentType: TextView = itemView.findViewById(R.id.tvPaymentType)
        val llPaid: LinearLayout = itemView.findViewById(R.id.llPaidAmount)

        companion object {
            fun create(parent: ViewGroup): TransactionViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.transaction_item, parent, false)
                return TransactionViewHolder(view)
            }
        }
    }

    class TransactionComparator : DiffUtil.ItemCallback<TransactionTable>() {
        override fun areItemsTheSame(
            oldItem: TransactionTable,
            newItem: TransactionTable
        ): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(
            oldItem: TransactionTable,
            newItem: TransactionTable
        ): Boolean {
            return oldItem.id == newItem.id
        }

    }

}
