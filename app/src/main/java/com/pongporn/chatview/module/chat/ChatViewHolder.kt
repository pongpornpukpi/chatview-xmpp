package com.pongporn.chatview.module.chat

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.pongporn.chatview.database.entity.HistoryChatEntity
import com.pongporn.chatview.model.ChatMessageModel
import kotlinx.android.synthetic.main.viewholder_chat_view.view.*

class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


    fun bindView(message: ChatMessageModel) {
        with(itemView) {
            textview_name.text = message.name
            textView3.text = message.message
            textview_time.text = message.timestamp
        }
    }
}
