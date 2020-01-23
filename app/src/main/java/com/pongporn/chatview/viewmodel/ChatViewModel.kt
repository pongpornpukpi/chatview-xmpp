package com.pongporn.chatview.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pongporn.chatview.database.ChatDatabase
import com.pongporn.chatview.database.entity.HistoryChatEntity
import com.pongporn.chatview.http.api.YoutubeApi
import com.pongporn.chatview.http.response.VideoDataResponseModel
import com.pongporn.chatview.utils.*
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.*
import org.jivesoftware.smack.MessageListener
import org.jivesoftware.smack.chat2.ChatManager
import org.jivesoftware.smack.packet.Message
import java.util.concurrent.TimeUnit
import kotlin.coroutines.CoroutineContext

class ChatViewModel constructor(
    var xmpp: XMPP,
    var chatDatabase: ChatDatabase,
    var pref: PreferenceUtils,
    var youtubeApi: YoutubeApi
) : ViewModel(),
    CoroutineScope {

    private val job = Job()
    val compositeDis by lazyOf(CompositeDisposable())
    var disposable: Disposable? = null
    var startTime: Int = 0
    var endTime: Int = 0
    var listenerMessage: MessageListener? = null

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    private val messageliveData: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    private val videoData: MutableLiveData<VideoDataResponseModel> by lazy { MutableLiveData<VideoDataResponseModel>() }

    fun addlistenerMulti() {
        viewModelScope.launch {
            receiveMultiMessage()
        }
    }

    private suspend fun receiveMultiMessage() {
        withContext(Dispatchers.IO) {
            listenerMessage = object : MessageListener {
                override fun processMessage(message: Message?) {
                    Log.d("app message Multi", message?.body ?: "null")
                    Log.d("app message Multi Time", message?.getChatTimestamp())
                    val fromMessage = message?.from?.resourceOrEmpty
                    if (message?.body != null) {
                        val his = HistoryChatEntity()
                        his.timeStamp = message.getChatTimestamp()
                        his.fromTo = fromMessage.toString()
                        his.message = message.body
                        chatDatabase.historyChatDao().insertHistoryChat(his)
                        messageliveData.postValue(message.body)
                    }
                }
            }

            xmpp.multiUserChat?.addMessageListener(listenerMessage)
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
    fun getVideoData(): LiveData<VideoDataResponseModel> = videoData

    fun getCountTime(startTime: Int, endTime: Int) {
        this.startTime = startTime
        this.endTime = endTime
        countTime()
    }

    fun updateStartTime(startTime: Int, endTime: Int) {
        this.startTime = startTime
        this.endTime = endTime
    }

    private fun countTime() {
        disposable = Observable.interval(0, 1, TimeUnit.SECONDS)
            .timeInterval()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                if (startTime <= endTime) {
                    startTime++
                    Log.d("app Time Start!", "${startTime}")
                    Log.d("app Time End!", "${endTime}")
                }
            }
        compositeDis.add(disposable!!)
    }

    fun getVideoDataRequest(id: String, key: String, part: String) {
        compositeDis.add(youtubeApi.getViedoDisplay(id, key, part).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                videoData.postValue(it)
            })
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
        compositeDis.dispose()
        disposable?.dispose()
    }

}