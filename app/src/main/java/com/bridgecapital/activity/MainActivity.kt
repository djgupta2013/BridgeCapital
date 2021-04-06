package com.bridgecapital.activity

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Base64
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.room.TypeConverter
import com.bridgecapital.R
import com.bridgecapital.UserApplication
import com.bridgecapital.db.table.TransactionTable
import com.bridgecapital.viewModel.TransactionViewModel
import com.bridgecapital.viewModel.TransactionViewModelFactory
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.android.synthetic.main.activity_main.*
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity(), View.OnClickListener {
    private val pickImageCamera = 1
    private val pickImageGallery = 2
    private var dialog: Dialog? = null
    private var uri: Uri? = null
    private var image = ""
    private var byteArray: ByteArray? = null
    private var isFullAmountReceived = true
    private val transactionViewModel: TransactionViewModel by viewModels {
        TransactionViewModelFactory((application as UserApplication).repository)
    }
    private var formattedDate = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setListener()

        val c: Date = Calendar.getInstance().time
        val df = SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault())
        formattedDate= df.format(c)

        //TODO check date and delete
        transactionViewModel.dateCheck(formattedDate)
    }

    private fun setListener() {
        ivAttachment.setOnClickListener(this)
        switchButton.setOnClickListener(this)
        btnCashIn.setOnClickListener(this)
        btnCashOut.setOnClickListener(this)
        btnViewAllTransaction.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivAttachment -> {
                selectImageDialog()
            }
            R.id.switchButton -> {
                if (switchButton.isChecked) {
                    edtPaidAmount.visibility = View.GONE
                    isFullAmountReceived = true
                } else {
                    edtPaidAmount.visibility = View.VISIBLE
                    isFullAmountReceived = false
                }
            }
            //TODO Add income data
            R.id.btnCashIn -> {
                if (checkValidation()) {
                    insertIncomeAndExpenseData("Income")
                }
            }
            //TODO Add Expense data
            R.id.btnCashOut -> {
                if (checkValidation()) {
                    insertIncomeAndExpenseData("Expense")
                }
            }
            R.id.btnViewAllTransaction -> {
                startActivity(Intent(this, TransactionListActivity::class.java))
            }
            R.id.ivClose -> {
                dialog?.dismiss()
            }
            R.id.llCamera -> {
                requestStoragePermission(true)
                dialog?.dismiss()
            }
            R.id.llGallery -> {
                requestStoragePermission(false)
                dialog?.dismiss()
            }
        }
    }

    //TODO Add Income and Expense data
    private fun insertIncomeAndExpenseData(transactionType: String) {
        val paidAmount = if (isFullAmountReceived) {
            edtAmount.text.toString().toInt()
        } else {
            if (edtPaidAmount.text.toString() == "") {
                0
            } else {
                edtPaidAmount.text.toString().toInt()
            }
        }
        val pendingAmount = (edtAmount.text.toString().toInt() - paidAmount)
        val model = TransactionTable(
            edtPartyName.text.toString(),
            edtPartyNumber.text.toString(),
            edtAmount.text.toString().toInt(),
            isFullAmountReceived,
            paidAmount,
            pendingAmount,
            image,
            transactionType,
            formattedDate
        )
        transactionViewModel.insert(model)
        simpleAlert("Data successful inserted.")
    }

    //TODO Check validation
    private fun checkValidation(): Boolean {
        return when {
            edtPartyName.text.toString().isEmpty() -> {
                edtPartyName.apply {
                    error = "Please enter Party name"
                    requestFocus()
                }
                false
            }
            edtPartyNumber.text.toString().isEmpty() -> {
                edtPartyNumber.apply {
                    error = "Please enter Party Number"
                    requestFocus()
                }
                false
            }
            edtAmount.text.toString().isEmpty() -> {
                edtAmount.apply {
                    error = "Please enter Amount"
                    requestFocus()
                }
                false
            }
            !switchButton.isChecked -> {
                if (edtPaidAmount.text.toString().isEmpty()) {
                    edtPaidAmount.apply {
                        error = "Please enter Paid Amount"
                        requestFocus()
                    }
                    false
                } else {
                    if ((edtAmount.text.toString()).toInt() < (edtPaidAmount.text.toString()).toInt()) {
                        edtPaidAmount.apply {
                            error = "Please enter less than amount of Total amount"
                            requestFocus()
                        }
                        false
                    } else {
                        true
                    }
                }
            }
            image == "" -> {
                Toast.makeText(this, "Please add Image", Toast.LENGTH_LONG).show()
                false
            }
            else -> true
        }
    }

    private fun simpleAlert(msg: String) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(msg)
        builder.setPositiveButton("OK") { _, _ ->
            edtPartyName.setText("")
            edtPartyNumber.setText("")
            edtAmount.setText("")
            edtPaidAmount.setText("")
            edtPaidAmount.visibility = View.GONE
            switchButton.isChecked = true
            uri = null
            image = ""
            ivAttachment.setImageResource(R.drawable.ic_add_photo_24)
        }
        builder.setCancelable(false)
        builder.show()
    }

    //TODO Show image select dialog
    private fun selectImageDialog() {
        dialog = Dialog(this)
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.setContentView(R.layout.image_select_type_dialog_layout)
        dialog?.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog?.window!!.setGravity(Gravity.CENTER)
        dialog?.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        //showDialog!!.window!!.attributes.windowAnimations=R.style.DialogTheme
        dialog?.setCancelable(false)
        dialog?.show()
        dialog?.findViewById<ImageView>(R.id.ivClose)?.setOnClickListener(this)
        dialog?.findViewById<LinearLayout>(R.id.llCamera)?.setOnClickListener(this)
        dialog?.findViewById<LinearLayout>(R.id.llGallery)?.setOnClickListener(this)

    }

    //TODO Request permission
    private fun requestStoragePermission(isCamera: Boolean) {
        Dexter.withActivity(this)
            .withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    // check if all permissions are granted
                    if (report.areAllPermissionsGranted()) {
                        if (isCamera) {
                            openCamera()
                        } else {
                            openGallery()
                        }
                    }
                    // check for permanent denial of any permission
                    if (report.isAnyPermissionPermanentlyDenied) {
                        // show alert dialog navigating to Settings
                        showSettingsDialog()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<com.karumi.dexter.listener.PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    token!!.continuePermissionRequest()
                }
            })
            .withErrorListener { error ->
                Toast.makeText(this, "Error occurred! ", Toast.LENGTH_SHORT)
                    .show()
            }
            .onSameThread()
            .check()
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        this.startActivityForResult(intent, pickImageCamera)
    }

    private fun openGallery() {
        val pickPhoto =
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        this.startActivityForResult(pickPhoto, pickImageGallery)
    }

    private fun showSettingsDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Need Permissions")
        builder.setMessage(
            "This app needs permission to use this feature. You can grant them in app settings."
        )
        builder.setPositiveButton(
            "GOTO SETTINGS"
        ) { dialog: DialogInterface, which: Int ->
            openSettings()
            dialog.cancel()
        }
        builder.setNegativeButton(
            "Cancel"
        ) { dialog: DialogInterface, which: Int -> dialog.cancel() }
        builder.show()
    }

    private fun openSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", "com.bridgecapital", null)
        intent.data = uri
        startActivityForResult(intent, 101)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == pickImageCamera && data != null) {
            try {
                val bitmap = data.extras!!["data"] as Bitmap?
                val bytes = ByteArrayOutputStream()
                bitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
                byteArray = bytes.toByteArray()
                uri = getImageUri(bitmap)
                try {
                    var inputStream: InputStream =
                        this.contentResolver.openInputStream(uri!!)!!

                    val bitmap1: Bitmap = BitmapFactory.decodeStream(inputStream)
                    image = bitMapToString(bitmap1)!!

                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                }
                ivAttachment.setImageURI(uri)
            } catch (e: Exception) {
                Log.e("pickImageCamera", "pickImageCamera Error...")
                e.printStackTrace()
            }

        } else if (requestCode == pickImageGallery && data != null) {
            val selectedImage = data.data
            try {
                uri = selectedImage
                ivAttachment.setImageURI(selectedImage)
                try {
                    var inputStream: InputStream =
                        this.contentResolver.openInputStream(selectedImage!!)!!

                    val bitmap: Bitmap = BitmapFactory.decodeStream(inputStream)
                    image = bitMapToString(bitmap)!!

                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                }


            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    @TypeConverter
    fun bitMapToString(bitmap: Bitmap): String? {
        val baos = ByteArrayOutputStream()
        bitmap.compress(CompressFormat.PNG, 50, baos)
        val b = baos.toByteArray()
        val temp: String = Base64.encodeToString(b, Base64.DEFAULT)
        return if (temp == null) {
            null
        } else temp
    }

    private fun getImageUri(inImage: Bitmap): Uri? {
        try {
            val bytes = ByteArrayOutputStream()
            inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
            val path = MediaStore.Images.Media.insertImage(
                this.contentResolver,
                inImage,
                "pic_${System.currentTimeMillis()}",
                null
            )
            MediaScannerConnection.scanFile(
                applicationContext, arrayOf<String>(path), null
            ) { path1, uri ->
                Log.e("ExternalStorage", "Scanned $path1:")
                Log.e("ExternalStorage", "-> uri=$uri")
            }
            Log.e("path", path)
            return Uri.parse(path)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            Log.e("finally", "finally")
        }
        return null
    }
}