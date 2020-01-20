package com.pongporn.chatview.userlist

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pongporn.chatview.R
import kotlinx.android.synthetic.main.activity_user_list.*

class UserListActivity : AppCompatActivity() {

    companion object {
        const val NAME_USER = "name_user"
    }

    private var userName : String? = null
    private val mAdapter by lazy { UserListAdapter() }
    private val listname =
        mutableListOf<UserListModel>(UserListModel(name = "kia.puk", isGroup = false), UserListModel(name = "nonnyzcsrt",isGroup = false),
            UserListModel(name = "Room25",isGroup = true))
    //    "kia.puk","nonnyzcsrt"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_list)
        userName = intent?.getStringExtra(NAME_USER)
        initRecyclerView()
    }

    private fun initRecyclerView() {
        recyclerview_list.apply {
            layoutManager = LinearLayoutManager(this@UserListActivity, RecyclerView.VERTICAL, false)
            adapter = mAdapter
        }

        for (index in 0 until listname.size) {
            if (listname.get(index).name?.equals(userName) == false) {
                mAdapter.addlist(listname.get(index))
            }
        }
    }

}