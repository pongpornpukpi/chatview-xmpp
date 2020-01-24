package com.pongporn.chatview.module.userlist

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pongporn.chatview.R
import com.pongporn.chatview.utils.PreferenceUtils
import com.pongporn.chatview.utils.XMPP
import kotlinx.android.synthetic.main.activity_user_list.*
import org.koin.android.ext.android.inject

class UserListActivity : AppCompatActivity() {

    companion object {
        const val NAME_USER = "name_user"
        const val NAME_ROOM = "name_room"
    }

    private var userName: String? = ""
    private var nameRoom: String? = ""
    private val mAdapter by lazy { UserListAdapter() }
    private val listname =
        mutableListOf<UserListModel>(
            UserListModel(name = "kia.puk", isGroup = false),
            UserListModel(name = "nonnyzcsrt", isGroup = false)
        )
    private val xmpp: XMPP by inject()
    private val preferenceUtils : PreferenceUtils by inject()
    //    "kia.puk","nonnyzcsrt"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_list)
        userName = intent?.getStringExtra(NAME_USER)
        nameRoom = intent?.getStringExtra(NAME_ROOM)
        listname.add(UserListModel(name = nameRoom, isGroup = true))
        initRecyclerView()
        initClick()
    }

    private fun initClick() {
        button2.setOnClickListener {
            if (xmpp.isConnect()) {
                xmpp.logOut()
                preferenceUtils.isFirst = false
                finish()
            } else {
                finish()
            }
        }
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

    override fun onBackPressed() {
        xmpp.logOut()
        preferenceUtils.isFirst = false
        super.onBackPressed()

    }

}