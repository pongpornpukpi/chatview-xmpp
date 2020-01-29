package com.pongporn.chatview.module.chat

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.pongporn.chatview.database.entity.HistoryChatEntity
import kotlinx.android.synthetic.main.viewholder_chat_view.view.*

class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


    fun bindView(message: String) {
        with(itemView) {
            textView3.text = message
        }
    }
}
