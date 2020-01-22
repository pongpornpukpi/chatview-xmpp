package com.pongporn.chatview.viewmodel

import android.text.format.DateFormat
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pongporn.chatview.utils.XMPP
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.*
import org.jivesoftware.smack.chat2.ChatManager
import org.jivesoftware.smack.packet.Message
import org.jivesoftware.smackx.delay.packet.DelayInformation
import java.util.concurrent.TimeUnit
import kotlin.coroutines.CoroutineContext

class ChatViewModel constructor(var xmpp: XMPP) : ViewModel(), CoroutineScope {

    private val job = Job()
    private var disposable: Disposable? = null
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
                Log.d("app message Multi Time", getChatTimestamp(message))
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
                    Log.d("app receiveMessage", "message :")
                    if (message.body != null) {
                        messageliveData.postValue("${message.from} : ${message.body}")
                    }
                }
        }
    }

    fun getmessage(): LiveData<String> = messageliveData

    fun getCountTime(startTime: Long, endTime: Long) {
        viewModelScope.launch {
            countTime(startTime, endTime)
        }
    }

    private suspend fun countTime(startTime: Long, endTime: Long) {
        withContext(Dispatchers.IO) {
            disposable = Observable.interval(startTime, endTime, TimeUnit.SECONDS).timeInterval()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {

                }
        }
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }

    private fun convertTimeMilliToString(dateInMilliseconds: Long): String {
        val dateFormat = "HH:mm"
        return DateFormat.format(dateFormat, dateInMilliseconds)
            .toString()
    }

    private fun getChatTimestamp(message: Message): String {
        val msg = message
        val ts: Long
        var timestamp: DelayInformation? = msg.getExtension("delay", "urn:xmpp:delay")
        if (timestamp == null)
            timestamp = msg.getExtension("x", "jabber:x:delay")

        ts = timestamp?.stamp?.time ?: System.currentTimeMillis()

        return convertTimeMilliToString(ts)
    }

}