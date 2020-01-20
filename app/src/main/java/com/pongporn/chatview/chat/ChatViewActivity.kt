package com.pongporn.chatview.chat

import android.os.Bundle
import android.os.Message
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pongporn.chatview.R
import com.pongporn.chatview.userlist.UserListModel
import com.pongporn.chatview.utils.XMPP
import kotlinx.android.synthetic.main.activity_chat_view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jivesoftware.smack.chat2.ChatManager
import org.koin.android.ext.android.inject

class ChatViewActivity : AppCompatActivity() {

    companion object {
        const val USER_NAME = "user_name"
    }

    private val chatAdapter by lazy { ChatViewAdapter() }
    private var userList: UserListModel? = null
    private var chatList = mutableListOf<String>()
    private val xmpp: XMPP by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_view)
        userList = intent.getParcelableExtra<UserListModel>(USER_NAME)
        if (userList?.isGroup == true) {
            xmpp.onCreateMultiChatGroupRoom("Room23")
            xmpp.onJoinMultiChatGroupRoom()
        } else {
            xmpp.onCreateOneOnOneChatRoom()
        }
        initRecyclerView()
        initClicked()
        initView()
    }

    private fun initClicked() {
        et_comment.setOnClickListener {

        }

        btn_post.setOnClickListener {
            if (userList?.isGroup == true) {
                xmpp.multiChatSendMessage(et_comment.text.toString())
            } else {
                xmpp.sendMessage(et_comment.text.toString(), "${userList?.name}@natchatserver")
            }
        }
    }

    private fun initView() {
        title = userList?.name
        receiveMessage()
    }

    private fun initRecyclerView() {
        recyclerview_chat.apply {
            layoutManager = LinearLayoutManager(this@ChatViewActivity, RecyclerView.VERTICAL, false)
            adapter = chatAdapter
        }
    }

    private fun receiveMessage() {
        if (userList?.isGroup == true) {
            GlobalScope.launch {
                withContext(Dispatchers.IO) {
                    xmpp.multiUserChat?.addMessageListener { message ->
                        Log.d("app message Multi", message?.body ?: "null")
                        if (message.body != null) {
                            chatList.add(message.body)
                            Log.d("app message list ", chatList.toString())
                        }
                    }
                }
                if (!chatList.isNullOrEmpty()) {
                    chatAdapter.clearList()
                    chatAdapter.addlist(chatList)
                }
            }
        } else {
            GlobalScope.launch {
                withContext(Dispatchers.IO) {
                    ChatManager.getInstanceFor(xmpp.connection)
                        .addIncomingListener { from, message, chat ->
                            Log.d("app receiveMessage", "message.getBody() :" + message?.body)
                            Log.d("app receiveMessage", "message.getFrom() :" + message?.from)
                            if (message.body != null) {
                                chatList?.add(message.body)
                                Log.d("app message list ", chatList.toString())
                            }
                        }
                }
                chatAdapter.clearList()
                chatAdapter.addlist(chatList)
            }
        }
    }
}