package com.pongporn.chatview.userlist

import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.pongporn.chatview.chat.ChatViewActivity
import kotlinx.android.synthetic.main.viewholder_user_list.view.*

class UserListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bindView(userListModel: UserListModel?) {
        with(itemView) {
            tv_name.text = userListModel?.name ?: ""
            tv_name.setOnClickListener {
                val intent = Intent(context, ChatViewActivity::class.java)
                intent.putExtra(ChatViewActivity.USER_NAME,userListModel)
                context.startActivity(intent)
            }
        }
    }

}
