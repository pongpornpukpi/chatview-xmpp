package com.pongporn.chatview.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pongporn.chatview.utils.XMPP
import kotlinx.coroutines.*
import org.jivesoftware.smack.chat2.ChatManager
import kotlin.coroutines.CoroutineContext

class ChatViewModel constructor(var xmpp: XMPP) : ViewModel(),CoroutineScope {

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    private val messageliveData: MutableLiveData<String> by lazy { MutableLiveData<String>() }

    fun addlistenerMulti() {
        viewModelScope.launch {
            receiveMultiMessage()
        }
    }

    private suspend fun receiveMultiMessage() {
        withContext(Dispatchers.IO) {
            xmpp.multiUserChat?.addMessageListener { message ->
                Log.d("app message Multi", message?.body ?: "null")
                val fromMessage = message.from.resourceOrEmpty
                if (message.body != null) {
                    messageliveData.postValue("$fromMessage : ${message.body}")
                }
            }
        }
    }

    fun addlistenerOneOnOne() {
        viewModelScope.launch {
            receiveOneOnOneMessage()
        }
    }

    private suspend fun receiveOneOnOneMessage() {
        withContext(Dispatchers.IO) {
            ChatManager.getInstanceFor(xmpp.connection)
                .addIncomingListener { from, message, chat ->
                    Log.d("app receiveMessage", "message.getBody() :" + message?.body)
                    Log.d("app receiveMessage", "message.getFrom() :" + message?.from)
                    if (message.body != null) {
                        messageliveData.postValue("${message.from} : ${message.body}")
                    }
                }
        }
    }

    fun getmessage(): LiveData<String> = messageliveData

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }

}