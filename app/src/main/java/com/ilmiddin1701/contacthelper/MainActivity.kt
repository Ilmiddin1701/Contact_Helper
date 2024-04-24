package com.ilmiddin1701.contacthelper

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.florent37.runtimepermission.kotlin.askPermission
import com.ilmiddin1701.contacthelper.adapters.RvAdapter
import com.ilmiddin1701.contacthelper.databinding.ActivityMainBinding
import com.ilmiddin1701.contacthelper.helper.MyButton
import com.ilmiddin1701.contacthelper.helper.MySwipeHelper
import com.ilmiddin1701.contacthelper.listener.MyButtonClickListener
import com.ilmiddin1701.contacthelper.models.Contact

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    lateinit var rvAdapter: RvAdapter
    lateinit var contactList: ArrayList<Contact>

    @SuppressLint("Range")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.rv.setHasFixedSize(true)
        binding.rv.layoutManager = LinearLayoutManager(this)

        contactList = ArrayList()

        object : MySwipeHelper(this, binding.rv, 130) {
            override fun instantiateMyButton(
                viewHolder: RecyclerView.ViewHolder, buffer: MutableList<MyButton>
            ) {
                buffer.add(
                    MyButton(this@MainActivity, "SMS", 30, R.drawable.ic_sms, Color.parseColor("#DD2371"),
                        object : MyButtonClickListener {
                            override fun onClick(pos: Int) {
                                val intent = Intent(this@MainActivity, SMSActivity::class.java)
                                intent.putExtra("key", contactList[pos])
                                startActivity(intent)
                            }
                        })
                )
                buffer.add(
                    MyButton(this@MainActivity, "Call", 30, R.drawable.ic_call_2, Color.parseColor("#F8CA2A"),
                        object : MyButtonClickListener {
                            override fun onClick(pos: Int) {
                                callPhone(pos)
                            }
                        })
                )
            }
        }
        readContact()
    }

    private fun callPhone(position:Int) {
        askPermission(Manifest.permission.CALL_PHONE){
            val phoneNumber = contactList[position].number
            val intent = Intent(Intent(Intent.ACTION_CALL))
            intent.data = Uri.parse("tel:$phoneNumber")
            startActivity(intent)
        }.onDeclined { e ->
            if (e.hasDenied()) {
                AlertDialog.Builder(this)
                    .setMessage("Ruxsat bermasangiz ilova ishlay olmaydi ruxsat bering...")
                    .setPositiveButton("Ha") { dialog, which ->
                        e.askAgain()
                    }
                    .setNegativeButton("Yo'q") { dialog, which ->
                        dialog.dismiss();
                    }
                    .show();
            }

            if(e.hasForeverDenied()) {
                e.goToSettings();
            }
        }
    }

    @SuppressLint("Range")
    @RequiresApi(Build.VERSION_CODES.O)
    fun readContact() {
        contactList = ArrayList()
        askPermission(Manifest.permission.READ_CONTACTS) {
            val contacts = contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
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

            rvAdapter = RvAdapter(object : RvAdapter.RvAction {
                override fun moreClick(contact: Contact, imageView: ImageView) {
                    Toast.makeText(this@MainActivity, contact.name, Toast.LENGTH_SHORT).show()
                }
            }, contactList)

            binding.rv.adapter = rvAdapter
        }.onDeclined { e ->
            if (e.hasDenied()) {

                AlertDialog.Builder(this)
                    .setMessage("Ruxsat bermasangiz ilova ishlay olmaydi ruxsat bering...")
                    .setPositiveButton("Ha") { dialog, which ->
                        e.askAgain();
                    }
                    .setNegativeButton("Yo'q") { dialog, which ->
                        dialog.dismiss()
                    }
                    .show()
            }

            if (e.hasForeverDenied()) {
                e.goToSettings()
            }
        }
    }
}