package com.pongporn.chatview.chat

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.viewholder_chat_view.view.*

class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


    fun bindView(message : String) {
        with(itemView) {
            textView3.text = message
        }
    }
}
