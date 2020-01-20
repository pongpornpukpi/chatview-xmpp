package com.pongporn.chatview.chat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pongporn.chatview.R

class ChatViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var listChat = mutableListOf<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.viewholder_chat_view,parent,false)
        return ChatViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listChat.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder) {
            is ChatViewHolder -> {
                holder.bindView(listChat.get(position))
            }
        }
    }

    fun clearList() {
        listChat.clear()
        notifyDataSetChanged()
    }

    fun addlist(listItem : MutableList<String>?) {
        if (listItem != null) {
            this.listChat.addAll(listItem)
        }
        notifyDataSetChanged()
    }

    fun addChat(itemChat: String?){
        itemChat?.let { this.listChat.add(it) }
        notifyDataSetChanged()
    }

}
