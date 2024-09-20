package com.ilmiddin1701.contacthelper

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.telephony.SmsManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.ilmiddin1701.contacthelper.databinding.ActivitySmsactivityBinding
import com.ilmiddin1701.contacthelper.models.Contact

@Suppress("DEPRECATION")
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
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
                if (binding.edtMatn.text.toString().isNotBlank()) {
                    val text = binding.edtMatn.text.toString()
                    val obj = SmsManager.getDefault()
                    obj.sendTextMessage(contact.number, null, text, null, null)
                    binding.edtMatn.text.clear()
                    Toast.makeText(this, "Xabar yuborildi", Toast.LENGTH_SHORT).show()
                }
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.SEND_SMS), 3)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 3) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val contact = intent.getSerializableExtra("key") as Contact
                if (binding.edtMatn.text.toString().isNotBlank()) {
                    val text = binding.edtMatn.text.toString()
                    val obj = SmsManager.getDefault()
                    obj.sendTextMessage(contact.number, null, text, null, null)
                    binding.edtMatn.text.clear()
                    Toast.makeText(this, "Xabar yuborildi", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}