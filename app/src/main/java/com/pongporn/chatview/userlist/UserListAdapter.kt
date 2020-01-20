package com.pongporn.chatview.userlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pongporn.chatview.R

class UserListAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var listItem = mutableListOf<UserListModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.viewholder_user_list,parent,false)
        return UserListViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listItem.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder) {
            is UserListViewHolder -> {
                holder.bindView(listItem.get(position))
            }
        }
    }

    fun addlist(listname: UserListModel) {
        this.listItem.add(listname)
        notifyDataSetChanged()
    }

}
