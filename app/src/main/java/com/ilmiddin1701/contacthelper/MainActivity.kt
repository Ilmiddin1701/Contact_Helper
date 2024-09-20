package com.ilmiddin1701.contacthelper

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ilmiddin1701.contacthelper.adapters.RvAdapter
import com.ilmiddin1701.contacthelper.databinding.ActivityMainBinding
import com.ilmiddin1701.contacthelper.helper.MyButton
import com.ilmiddin1701.contacthelper.helper.MySwipeHelper
import com.ilmiddin1701.contacthelper.listener.MyButtonClickListener
import com.ilmiddin1701.contacthelper.models.Contact

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private lateinit var rvAdapter: RvAdapter
    lateinit var contactList: ArrayList<Contact>

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.rv.setHasFixedSize(true)
        binding.rv.layoutManager = LinearLayoutManager(this)

        contactList = ArrayList()

        // Qo'ng'iroq qilish va SMS tugmalari uchun Swipe helper
        object : MySwipeHelper(this, binding.rv, 130) {
            override fun instantiateMyButton(
                viewHolder: RecyclerView.ViewHolder, buffer: MutableList<MyButton>
            ) {
                buffer.add(
                    MyButton(this@MainActivity,
                        "SMS",
                        30,
                        R.drawable.ic_sms,
                        Color.parseColor("#DD2371"),
                        object : MyButtonClickListener {
                            override fun onClick(pos: Int) {
                                val intent = Intent(this@MainActivity, SMSActivity::class.java)
                                intent.putExtra("key", contactList[pos])
                                startActivity(intent)
                            }
                        })
                )
                buffer.add(
                    MyButton(this@MainActivity,
                        "Call",
                        30,
                        R.drawable.ic_call_2,
                        Color.parseColor("#F8CA2A"),
                        object : MyButtonClickListener {
                            override fun onClick(pos: Int) {
                                callPhone(pos)
                            }
                        })
                )
            }
        }
        checkAndRequestPermissions()
    }

    // Qo'ng'iroq qilish funksiyasi
    private fun callPhone(pos: Int) {
        val phoneNumber = contactList[pos].number
        val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$phoneNumber"))
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            startActivity(intent)
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CALL_PHONE), 2)
        }
    }

    // Ruxsatlarni tekshirish va so'rash funksiyasi
    private fun checkAndRequestPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_CONTACTS), 1)
        } else {
            readContact()
        }
    }

    // Kontaktlarni o'qish funksiyasi
    @SuppressLint("Range")
    private fun readContact() {
        contactList = ArrayList()
        val contacts = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            null
        )
        while (contacts!!.moveToNext()) {
            val contact = Contact(
                contacts.getString(contacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)),
                contacts.getString(contacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
            )
            contactList.add(contact)
        }
        contacts.close()

        rvAdapter = RvAdapter(contactList)
        binding.rv.adapter = rvAdapter
    }

    // Ruxsat natijalarini qayta ishlash
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val dialog = AlertDialog.Builder(this).create()
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                readContact()
            } else {
                // Kontaktlarni o'qish uchun ruxsat berilmagan bo'lsa
                dialog.setMessage("Kontaktlarni o'qish uchun ruxsat berishingiz kerak!")
                dialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK") { _, _ -> dialog.cancel() }
                dialog.show()
            }
        } else if (requestCode == 2) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Agar qo'ng'iroq qilish ruxsati berilgan bo'lsa, uni yana qo'ng'iroq qilish paytida ishlatish mumkin
            } else {
                // Qo'ng'iroq qilish uchun ruxsat berilmagan bo'lsa
                val dialog1 = AlertDialog.Builder(this).create()
                dialog1.setMessage("Qo'ng'iroq qilish uchun ruxsat berishingiz kerak!")
                dialog1.setButton(AlertDialog.BUTTON_POSITIVE, "OK") { _, _ -> dialog.cancel() }
                dialog1.show()
            }
        }
    }
}