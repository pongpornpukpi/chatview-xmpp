package com.pongporn.chatview.chat

import android.os.Bundle
import android.os.Message
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pongporn.chatview.R
import com.pongporn.chatview.userlist.UserListModel
import com.pongporn.chatview.utils.XMPP
import com.pongporn.chatview.viewmodel.ChatViewModel
import kotlinx.android.synthetic.main.activity_chat_view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jivesoftware.smack.chat2.ChatManager
import org.jivesoftware.smack.tcp.XMPPTCPConnection
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class ChatViewActivity : AppCompatActivity() {

    companion object {
        const val USER_NAME = "user_name"
    }

    private val chatAdapter by lazy { ChatViewAdapter() }
    private var userList: UserListModel? = null
    private var chatList = mutableListOf<String>()
    private val xmpp: XMPP by inject()
    private val viewModel: ChatViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_view)
        userList = intent.getParcelableExtra<UserListModel>(USER_NAME)
        if (userList?.isGroup == true) {
            xmpp.onCreateMultiChatGroupRoom(userList?.name)
            xmpp.onJoinMultiChatGroupRoom()
        }
        initObserver()
        initRecyclerView()
        initClicked()
        initView()
    }

    private fun initObserver() {
        viewModel.getmessage().observe(this, Observer<String> {
            chatList.add(it)
            chatAdapter.clearList()
            chatAdapter.addlist(chatList)
        })
        if (userList?.isGroup == true) {
            viewModel.addlistenerMulti()
        } else {
            viewModel.addlistenerOneOnOne()
        }
    }

    private fun initClicked() {
        et_comment.setOnClickListener {

        }

        btn_post.setOnClickListener {
            if (userList?.isGroup == true) {
                xmpp.multiChatSendMessage(et_comment.text.toString())
            } else {
                xmpp.sendMessage(et_comment.text.toString(), "${userList?.name}@natchatserver")
                chatList.add("${userList?.name} : ${et_comment.text.toString()}")
                chatAdapter.clearList()
                chatAdapter.addlist(chatList)
            }
        }
    }

    private fun initView() {
//        receiveMessage()
        title = userList?.name
    }

    private fun initRecyclerView() {
        recyclerview_chat.apply {
            layoutManager = LinearLayoutManager(this@ChatViewActivity, RecyclerView.VERTICAL, false)
            adapter = chatAdapter
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        xmpp.leaveChatRoom()
    }
}