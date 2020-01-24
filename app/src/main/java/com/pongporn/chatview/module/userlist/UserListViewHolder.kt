package com.pongporn.chatview.module.userlist

import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.pongporn.chatview.module.chat.ChatViewActivity
import kotlinx.android.synthetic.main.viewholder_user_list.view.*

class UserListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bindView(userListModel: UserListModel?, userListCallBack: (UserListModel?) -> Unit) {
        with(itemView) {
            tv_name.text = userListModel?.name ?: ""
            tv_name.setOnClickListener {
                userListCallBack(userListModel)
            }
        }
    }

}
