package com.ilmiddin1701.contacthelper.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.ilmiddin1701.contacthelper.databinding.ItemRvBinding
import com.ilmiddin1701.contacthelper.models.Contact

class RvAdapter(var rvAction: RvAction, var list: ArrayList<Contact>) : RecyclerView.Adapter<RvAdapter.Vh>() {

        inner class Vh(var itemRvBinding: ItemRvBinding) : RecyclerView.ViewHolder(itemRvBinding.root){
            fun onBinding(contact: Contact){
                itemRvBinding.tvName.text = contact.name
                itemRvBinding.tvNumber.text = contact.number
                itemRvBinding.btnMore.setOnClickListener {
                    rvAction.moreClick(contact, itemRvBinding.btnMore)
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
            return Vh(ItemRvBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }

        override fun getItemCount(): Int = list.size

        override fun onBindViewHolder(holder: Vh, position: Int) {
            holder.onBinding(list[position])
        }
    interface RvAction{
        fun moreClick(contact: Contact, imageView: ImageView)
    }
}