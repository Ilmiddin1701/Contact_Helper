package com.ilmiddin1701.contacthelper

import android.Manifest
import android.os.Bundle
import android.telephony.SmsManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.github.florent37.runtimepermission.kotlin.askPermission
import com.ilmiddin1701.contacthelper.databinding.ActivitySmsactivityBinding
import com.ilmiddin1701.contacthelper.models.Contact

@Suppress("DEPRECATION", "CAST_NEVER_SUCCEEDS")
class SMSActivity : AppCompatActivity() {
    private val binding by lazy { ActivitySmsactivityBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            finish()
        }

        val contact = intent.getSerializableExtra("key") as Contact
        binding.txtNameSms.text = contact.name
        binding.txtNumberSms.text = contact.number

        binding.btnSend.setOnClickListener {
            askPermission(Manifest.permission.SEND_SMS){
                val matn = binding.edtMatn.text.toString()
                var obj = SmsManager.getDefault()
                obj.sendTextMessage(contact.number, null,  matn, null, null)
                Toast.makeText(this, "Xabar yuborildi", Toast.LENGTH_SHORT).show()
            }.onDeclined { e ->
                if (e.hasDenied()) {
                    AlertDialog.Builder(this)
                        .setMessage("Ruxsat bermasangiz ilova ishlay olmaydi ruxsat bering...")
                        .setPositiveButton("yes") { dialog, which ->
                            e.askAgain()
                        }
                        .setNegativeButton("no") { dialog, which ->
                            dialog.dismiss()
                        }
                        .show()
                }
                if(e.hasForeverDenied()) {
                    e.goToSettings()
                }
            }
        }
    }
}