package com.pongporn.chatview.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pongporn.chatview.database.ChatDatabase
import com.pongporn.chatview.database.entity.HistoryChatEntity
import com.pongporn.chatview.http.api.GoogleApi
import com.pongporn.chatview.http.api.YoutubeApi
import com.pongporn.chatview.http.request.InsertVideoLiveChatMessageRequest
import com.pongporn.chatview.http.response.InsertVideoLiveMessage
import com.pongporn.chatview.http.response.VideoDataResponseModel
import com.pongporn.chatview.http.response.VideoLiveMessageResponse
import com.pongporn.chatview.http.response.VideoLiveStreamingDetailResponseModel
import com.pongporn.chatview.model.ChatMessageModel
import com.pongporn.chatview.utils.*
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.*
import okhttp3.Connection
import org.jivesoftware.smack.MessageListener
import org.jivesoftware.smack.chat2.ChatManager
import org.jivesoftware.smack.packet.Message
import org.jivesoftware.smackx.mam.MamManager
import java.lang.Exception
import java.util.concurrent.TimeUnit
import kotlin.coroutines.CoroutineContext
import org.jivesoftware.smackx.mam.element.MamFinIQ
import org.jivesoftware.smackx.forward.packet.Forwarded
import org.jivesoftware.smackx.mam.MamManager.MamQueryResult


class ChatViewModel constructor(
    var xmpp: XMPP,
    var chatDatabase: ChatDatabase,
    var pref: PreferenceUtils,
    var youtubeApi: YoutubeApi,
    var googleApi: GoogleApi
) : ViewModel(),
    CoroutineScope {

    private val job = Job()
    val compositeDis by lazyOf(CompositeDisposable())
    var disposable: Disposable? = null
    var disposableMessages: Disposable? = null
    var startTime: Int = 0
    var endTime: Int = 0
    var listenerMessage: MessageListener? = null
    var tempMessageList = listOf<Message>()
    var uid: String? = null
    var doMoreLoading: Boolean? = false
    var messageStr: ChatMessageModel? = null
    var nextPageToken = ""
    private val listHistory = mutableListOf<ChatMessageModel>()

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    private val messageliveData: MutableLiveData<ChatMessageModel> by lazy { MutableLiveData<ChatMessageModel>() }
    private val videoLiveStreamingDetail: MutableLiveData<VideoLiveStreamingDetailResponseModel> by lazy { MutableLiveData<VideoLiveStreamingDetailResponseModel>() }
    private val videoLiveStreamingMessage: MutableLiveData<VideoLiveMessageResponse> by lazy { MutableLiveData<VideoLiveMessageResponse>() }
    private val videoLiveStreamingMessageRealTime: MutableLiveData<VideoLiveMessageResponse> by lazy { MutableLiveData<VideoLiveMessageResponse>() }
    private val messageHistoryList = MutableLiveData<List<ChatMessageModel>>()
    private val insertLiveChat: MutableLiveData<InsertVideoLiveMessage> by lazy { MutableLiveData<InsertVideoLiveMessage>() }

    fun getmessage(): LiveData<ChatMessageModel> = messageliveData
    fun getVideoLiveStreamDetail(): LiveData<VideoLiveStreamingDetailResponseModel> = videoLiveStreamingDetail
    fun getVideoLiveStreamMessage(): LiveData<VideoLiveMessageResponse> = videoLiveStreamingMessage
    fun getVideoLiveStreamMessageRealTime(): LiveData<VideoLiveMessageResponse> = videoLiveStreamingMessageRealTime
    fun insertLiveChat(): LiveData<InsertVideoLiveMessage> = insertLiveChat
    fun getmessageHistory(): LiveData<List<ChatMessageModel>> = messageHistoryList

    fun addlistenerMulti() {
        viewModelScope.launch {
            receiveMultiMessage()
        }
    }

    private fun receiveMultiMessage() {
        disposableMessages = addListenerMessage().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                val fromMessage = it?.from?.resourceOrEmpty
                messageStr =
                    ChatMessageModel() //"${it.getChatTimestamp()} : $fromMessage :: ${it.body}"
                messageStr?.timestamp = it.getChatTimestamp()
                messageStr?.name = fromMessage.toString()
                messageStr?.message = it.body
                listHistory.add(0, messageStr!!)
                messageliveData.value = messageStr
            }, {
                Log.d("appError", it.toString())
            }, {

            })
    }

    private fun addListenerMessage(): Observable<Message> {
        return Observable.create<Message> { source ->
            try {
                listenerMessage = MessageListener { message ->
                    Log.d("appmessageMulti", message?.body ?: "null")
                    Log.d("appmessageMultiTime", message?.getChatTimestamp())
                    if (message?.body != null) {
                        source.onNext(message)
                    }
                }

                xmpp.multiUserChat?.addMessageListener(listenerMessage)
            } catch (e: Exception) {
                source.onError(e)
            }
        }
    }

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
                    Log.d(
                        "appTimeStart!",
                        "${startTime} Second = ${startTime.convertSecondToMinutes()} Minute"
                    )
                    Log.d(
                        "appTimeEnd!",
                        "${endTime} Second = ${endTime.convertSecondToMinutes()} Minute"
                    )
                }
            }
        compositeDis.add(disposable!!)
    }

    fun getVideoLiveStreamingDetailRequest(id: String,key: String, part: String) {
        compositeDis.add(youtubeApi.getViedoLiveStraming(id,key, part).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnError {
                println("Error : ${it.printStackTrace()}")
            }
            .subscribe {
                videoLiveStreamingDetail.postValue(it)
            }
        )
    }

    fun getVideoLiveMessageRequest(id: String,key: String, part: String) {
        compositeDis.add(youtubeApi.getMessageLiveStreaming(id,key, part).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnError {
                println("Error : ${it.printStackTrace()}")
            }
            .subscribe {
                videoLiveStreamingMessage.postValue(it)
            })
    }

    fun insertVideoLiveMessage(accessToken : String,part: String,key: String,request: InsertVideoLiveChatMessageRequest) {
        compositeDis.add(youtubeApi.insertLiveChatMessages(accessToken,part,key,request).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnError {
                println("Error : ${it.printStackTrace()}")
            }
            .subscribe {
                insertLiveChat.postValue(it)
            })
    }

    fun authGoogle() {
        compositeDis.add(googleApi.oAuthGoogleAPI().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnError {
                println("Error : ${it.printStackTrace()}")
            }
            .subscribe {
                println("Auth $it")
            })
    }

    fun getVideoLiveMessageRealTimeRequest(id: String,key: String, part: String,time : Long,nextPageToken : String) {
//        compositeDis.add(youtubeApi.getMessageLiveStreaming(id,key, part).subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe {
//                videoLiveStreamingMessage.postValue(it)
//            })

        this.nextPageToken = nextPageToken

        disposable = Observable.interval(time, time, TimeUnit.MILLISECONDS)
            .timeInterval()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                youtubeApi.getMessageLiveStreamingRealTime(id,key, part,this.nextPageToken).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        videoLiveStreamingMessageRealTime.postValue(it)
                    }
            }
        compositeDis.add(disposable!!)
    }

    fun updateNextPageToken(nextPageToken : String) {
        this.nextPageToken = nextPageToken
    }

    fun loadHistory() {

        disposableMessages = getObservableMessages()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ listOfMessages ->
                tempMessageList = listOfMessages
                for (index in tempMessageList.size downTo 1 step 1) {
                    println("appMam -> ${tempMessageList.size}")
                    println("appMam -> ${tempMessageList.get(index - 1).body}")
                    var messagelist = ChatMessageModel()
                    messagelist.timestamp = tempMessageList.get(index - 1).getChatTimestamp()
                    messagelist.name =
                        tempMessageList.get(index - 1).from?.resourceOrEmpty.toString()
                    messagelist.message = tempMessageList.get(index - 1).body
                    listHistory.add(messagelist)
//                    listHistory.add("${tempMessageList.get(index-1).getChatTimestamp()} : ${tempMessageList.get(index-1).from?.resourceOrEmpty} :: ${tempMessageList.get(index-1).body}")
                }
            }, { t ->
                Log.d("appMamError", t.toString())
            }, {
                messageHistoryList.value = listHistory
            })
    }

    // number_of_messages_to_fetch it's a limit of messages to be fetched eg. 20.
    private fun getObservableMessages(): Observable<List<Message>> {
        return Observable.create<List<Message>> { source ->
            try {
                val mamQuery = xmpp.mamManager?.queryMostRecentPage(xmpp.multiUserJid, 20)
                if (mamQuery?.messageCount == 0 || mamQuery?.messageCount!! < 20) {
                    uid = ""
                    doMoreLoading = false
                } else {
                    uid = mamQuery.mamResultExtensions?.get(0)?.id
                    doMoreLoading = true
                }
                source.onNext(mamQuery.messages!!)

            } catch (e: Exception) {
                if (!xmpp.connection.isConnected) {
                    source.onError(e)
                } else {
                    Log.e("ChatDetail", "Connection closed")
                }
            }
            source.onComplete()
        }
    }

    fun getMoreMessages() {
        if (doMoreLoading == true) {
            val mamQueryArgs = MamManager.MamQueryArgs.builder()
                .limitResultsToJid(xmpp.multiUserJid)
                .beforeUid(uid)
                .build()

            disposableMessages = getObservableMoreMessages(mamQueryArgs)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ listOfMessages ->
                    tempMessageList = listOfMessages
                    for (index in tempMessageList.size downTo 1 step 1) {
                        var messagelist = ChatMessageModel()
                        messagelist.timestamp = tempMessageList.get(index - 1).getChatTimestamp()
                        messagelist.name =
                            tempMessageList.get(index - 1).from?.resourceOrEmpty.toString()
                        messagelist.message = tempMessageList.get(index - 1).body
                        listHistory.add(messagelist)
//                        listHistory.add("${tempMessageList.get(index - 1).getChatTimestamp()} : ${tempMessageList.get(index - 1).from?.resourceOrEmpty} :: ${tempMessageList.get(index - 1).body}")
                        println("appMam -> initMam -> OnNext -> ${tempMessageList.get(index - 1).body}")
                    }
                }, { t ->
                    Log.v("message", "FailinitmamError")
                    Log.e("message", "-> initMam -> onError ->", t)
                }, {
                    messageHistoryList.value = listHistory
                })
        }
    }

    private fun getObservableMoreMessages(mamQueryArgs: MamManager.MamQueryArgs): Observable<List<Message>> {
        return Observable.create<List<Message>> { source ->
            try {
                val mamQuery = xmpp.mamManager?.queryArchive(mamQueryArgs)
                if (mamQuery?.messageCount == 0 || mamQuery?.messageCount!! < 50) {
                    uid = ""
                    doMoreLoading = false
                } else {
                    uid = mamQuery.mamResultExtensions[0].id
                    doMoreLoading = true
                }
                source.onNext(mamQuery.messages)
            } catch (e: Exception) {
                val connection = xmpp.connection
                if (!connection.isConnected) {
                    source.onError(e)
                } else {
                    Log.e("ChatDetail", "Connection closed")
                }
            }
            source.onComplete()
        }
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
        compositeDis.dispose()
        disposable?.dispose()
    }

}