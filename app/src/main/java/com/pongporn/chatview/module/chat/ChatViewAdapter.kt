package com.pongporn.chatview.module.chat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pongporn.chatview.R
import com.pongporn.chatview.database.entity.HistoryChatEntity
import com.pongporn.chatview.model.ChatMessageModel

class ChatViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var listChat = mutableListOf<ChatMessageModel>()

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

    fun removeLastList() {
        listChat.removeAt(0)
        notifyDataSetChanged()
    }

    fun addOne(message : ChatMessageModel) {
        this.listChat.add(0,message)
        notifyDataSetChanged()
    }

    fun addlist(listItem : MutableList<ChatMessageModel>?) {
        if (listItem != null) {
            this.listChat.addAll(listItem)
        }
        notifyDataSetChanged()
    }


}
