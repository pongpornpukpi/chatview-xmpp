package com.pongporn.chatview.module.chat

import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.ButterKnife
import com.google.android.material.snackbar.Snackbar
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayerSupportFragment
import com.pongporn.chatview.R
import com.pongporn.chatview.database.ChatDatabase
import com.pongporn.chatview.http.response.VideoDataResponseModel
import com.pongporn.chatview.model.ChatMessageModel
import com.pongporn.chatview.module.userlist.UserListModel
import com.pongporn.chatview.utils.*
import com.pongporn.chatview.utils.EmoticonLiveReaction.Emoticons
import com.pongporn.chatview.viewmodel.ChatViewModel
import com.pongporn.chatview.widgets.CustomEditText
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.FlowableEmitter
import io.reactivex.FlowableOnSubscribe
import io.reactivex.schedulers.Timed
import kotlinx.android.synthetic.main.activity_chat_view.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription

class ChatViewActivity : AppCompatActivity() {

    companion object {
        const val USER_NAME = "user_name"
        const val VIDEO_ID = "Sa2rsOxEtIA"
        //        Sa2rsOxEtIA,Z8NdLhqYk_A
        const val YOUTUBE_API_KEY = "AIzaSyAAvHB1OGvfLpgwLVvKMY3Li58g4XtGZGk"
    }

    private val xmpp: XMPP by inject()
    private val viewModel: ChatViewModel by viewModel()
    private val chatDatabase: ChatDatabase by inject()
    private val chatAdapter by lazy { ChatViewAdapter() }

    private var userList: UserListModel? = null
    private var chatList = mutableListOf<ChatMessageModel>()
    private var newPositionMillis: Int = 0

    lateinit var youTubePlayerInit: YouTubePlayer.OnInitializedListener
    lateinit var youtubePlayerFillScreen: YouTubePlayer.OnFullscreenListener

    private var isUserScrolling = false
    private var isListGoingUp = true
    private var emoticonSubscription: Subscription? = null
    private var subscriber : Subscriber<Timed<Emoticons>>? = null
    private val MINIMUM_DURATION_BETWEEN_EMOTICONS = 300 // in milliseconds

    private var emoticonClickAnimation: Animation? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_view)
        userList = intent.getParcelableExtra<UserListModel>(USER_NAME)
        initObserver()
        initListener()
        initView()
        ButterKnife.bind(this)
        if (userList?.isGroup == true) {
            xmpp.onCreateMultiChatGroupRoom(userList?.name)
            xmpp.onJoinMultiChatGroupRoom()
            xmpp.initMam()
            if (xmpp.isJoined() == true) {
                viewModel.addlistenerMulti()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        //Create an instance of FlowableOnSubscribe which will convert click events to streams
        val flowableOnSubscribe = object : FlowableOnSubscribe<Emoticons> {
            override fun subscribe(emitter: FlowableEmitter<Emoticons>) {
                convertClickEventToStream(emitter)
            }
        }
        //Give the backpressure strategy as BUFFER, so that the click items do not drop.
        val emoticonsFlowable = Flowable.create(flowableOnSubscribe, BackpressureStrategy.BUFFER)
        //Convert the stream to a timed stream, as we require the timestamp of each event
        val emoticonsTimedFlowable = emoticonsFlowable.timestamp()
        subscriber = getSubscriber()
        //Subscribe
        emoticonsTimedFlowable.subscribeWith(subscriber)
    }

    private fun getSubscriber(): Subscriber<Timed<Emoticons>> {
        return object : Subscriber<Timed<Emoticons>> {
            override fun onSubscribe(s: Subscription) {
                emoticonSubscription = s
                emoticonSubscription?.request(1)

                // for lazy evaluation.
                custom_view?.initView(this@ChatViewActivity)
            }

            override fun onNext(timed: Timed<Emoticons>) {

                custom_view?.addView(timed.value())

                val currentTimeStamp = System.currentTimeMillis()
                val diffInMillis = currentTimeStamp - (timed as Timed<*>).time()
                if (diffInMillis > MINIMUM_DURATION_BETWEEN_EMOTICONS) {
                    emoticonSubscription?.request(1)
                } else {
                    val handler = Handler()
                    handler.postDelayed({
                            emoticonSubscription?.request(1)
                        },
                        MINIMUM_DURATION_BETWEEN_EMOTICONS - diffInMillis
                    )
                }
            }

            override fun onError(t: Throwable) {
                //Do nothing
            }

            override fun onComplete() {
                emoticonSubscription?.cancel()
            }
        }
    }

    private fun initListener() {
        youtubePlayerFillScreen = YouTubePlayer.OnFullscreenListener {

        }

        youTubePlayerInit = object : YouTubePlayer.OnInitializedListener {
            override fun onInitializationSuccess(
                provide: YouTubePlayer.Provider?,
                youTubePlayer: YouTubePlayer?,
                wasRestored: Boolean
            ) {
                youTubePlayer?.loadVideo(VIDEO_ID)
                youTubePlayer?.setOnFullscreenListener(youtubePlayerFillScreen)

                youTubePlayer?.setPlaybackEventListener(object :
                    YouTubePlayer.PlaybackEventListener {
                    override fun onSeekTo(newPositionMillis: Int) {
                        Log.d("youtube", "onSeekTo $newPositionMillis")
                    }

                    override fun onBuffering(isBuffering: Boolean) {
                        Log.d("youtube", "onBuffering $isBuffering")
                    }

                    override fun onPlaying() {
                        Log.d("youtube", "onPlaying")
                        println("TimeOver : ${youTubePlayer.currentTimeMillis.convertMillisToDataTime()}")
                        newPositionMillis = youTubePlayer.currentTimeMillis.convertMillisToSecond()
                        if (viewModel.disposable == null) {
                            viewModel.getCountTime(
                                youTubePlayer.currentTimeMillis.convertMillisToSecond(),
                                youTubePlayer.durationMillis.convertMillisToSecond()
                            )
                        } else {
                            viewModel.updateStartTime(
                                newPositionMillis,
                                youTubePlayer.durationMillis.convertMillisToSecond()
                            )
                        }
                    }

                    override fun onStopped() {
                        Log.d("youtube", "onStopped")
                    }

                    override fun onPaused() {
                        Log.d("youtube", "onPaused")
                        newPositionMillis = youTubePlayer.currentTimeMillis.convertMillisToSecond()
                    }
                })

                youTubePlayer?.setPlayerStateChangeListener(object :
                    YouTubePlayer.PlayerStateChangeListener {
                    override fun onAdStarted() {
                        Log.d("youtube", "onAdStarted")

                    }

                    override fun onLoading() {
                        Log.d("youtube", "onLoading")

                    }

                    override fun onVideoStarted() {
                        Log.d("youtube", "onVideoStarted")
                    }

                    override fun onLoaded(p0: String?) {
                        Log.d("youtube", "onLoaded $p0")

                    }

                    override fun onVideoEnded() {
                        Log.d("youtube", "onVideoEnded")
                        newPositionMillis = youTubePlayer.currentTimeMillis.convertMillisToSecond()
                        viewModel.updateStartTime(
                            newPositionMillis,
                            youTubePlayer.durationMillis.convertMillisToSecond()
                        )
                    }

                    override fun onError(error: YouTubePlayer.ErrorReason?) {
                        Log.d("youtube", "onError $error")
                    }

                })
            }

            override fun onInitializationFailure(
                provider: YouTubePlayer.Provider?,
                error: YouTubeInitializationResult?
            ) {
                Snackbar.make(scroll_horizon, "YouTube!! Init Failure. $error", Snackbar.LENGTH_SHORT)
                    .show()
            }
        }

        tv_editText.setOnClickListener {
            xmpp.showSoftKeyboard(this)
            ln_chat_view.visibility = View.VISIBLE
            et_comment.requestFocus()
        }

        et_comment.setOnClickListener {

        }

        et_comment.setHandleDismissingKeyboard(object : CustomEditText.onHandleDismissingKeyboard{
            override fun dismissKeyboard() {
                xmpp.hideSoftKeyboard(this@ChatViewActivity)
                ln_chat_view.visibility = View.INVISIBLE
            }
        })

        btn_post.setOnClickListener {
            xmpp.multiChatSendMessage(et_comment.text.toString())
            et_comment.setText("")
            xmpp.hideSoftKeyboard(this@ChatViewActivity)
            ln_chat_view.visibility = View.INVISIBLE
        }
    }

    private fun convertClickEventToStream(emitter: FlowableEmitter<Emoticons>) {
        like_emoticon?.setOnAnimateClickListener {
//            val likeEmo = R.drawable.ic_like
//            likeEmo.flyEmoji(this)
            doOnClick(it, emitter, Emoticons.LIKE)
        }

        love_emoticon?.setOnAnimateClickListener {
//            val loveEmo = R.drawable.ic_love
//            loveEmo.flyEmoji(this)
            doOnClick(it, emitter, Emoticons.LOVE)
        }

        sad_emoticon?.setOnAnimateClickListener {
//            val sadEmo = R.drawable.ic_sad
//            sadEmo.flyEmoji(this)
            doOnClick(it, emitter, Emoticons.SAD)
        }

        wow_emoticon?.setOnAnimateClickListener {
            doOnClick(it, emitter, Emoticons.WOW)
        }

        angry_emoticon?.setOnAnimateClickListener {
            doOnClick(it, emitter, Emoticons.ANGRY)
        }
    }

    private fun initObserver() {
        viewModel.getmessage().observe(this, Observer<ChatMessageModel> {
            recyclerview_chat.scrollToPosition(0)
            chatAdapter.addOne(ChatMessageModel("","",""))
            chatAdapter.notifyDataSetChanged()
            Handler().postDelayed({
                chatAdapter.removeLastList()
                chatAdapter.addOne(it)
                recyclerview_chat.scrollToPosition(0)
            },500)
        })

        viewModel.getVideoData().observe(this, Observer<VideoDataResponseModel> {
            if (it.items?.get(0)?.snippet?.liveBroadcastContent.equals("none")) {
                viewModel.loadHistory()
            }
        })

        viewModel.getmessageHistory().observe(this, Observer<List<ChatMessageModel>> {
            chatList.clear()
            chatList.addAll(it)
            chatAdapter.clearList()
            chatAdapter.addlist(chatList)
        })

        viewModel.getVideoDataRequest(id = VIDEO_ID, key = YOUTUBE_API_KEY, part = "snippet")
    }

    private fun initView() {
        recyclerview_chat.apply {
            layoutManager = LinearLayoutManager(this@ChatViewActivity, RecyclerView.VERTICAL, true)
            adapter = chatAdapter

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (isUserScrolling) {
                        println("app : $dy")
                        isListGoingUp = dy <= 0
                    }
                }

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                        isUserScrolling = true
                        if (isListGoingUp) {
                            if ((layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition() + 1 == recyclerView.adapter?.itemCount) {
                                val handler = Handler()
                                handler.postDelayed({
                                    viewModel.getMoreMessages()
                                }, 0)
                            }
                        }
                    }
                }
            })
        }

        title = userList?.name

        val frag =
            supportFragmentManager.findFragmentById(R.id.youtube_fragment) as YouTubePlayerSupportFragment?
        frag?.initialize(YOUTUBE_API_KEY, youTubePlayerInit)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (newConfig.keyboardHidden == Configuration.HARDKEYBOARDHIDDEN_NO) {
            Toast.makeText(this, "keyboard visible", Toast.LENGTH_SHORT).show()
        } else if (newConfig.keyboardHidden == Configuration.HARDKEYBOARDHIDDEN_YES) {
            Toast.makeText(this, "keyboard hidden", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onStop() {
        super.onStop()
        emoticonSubscription?.cancel()
    }

    private fun doOnClick(view: View, emitter: FlowableEmitter<Emoticons>, emoticons: Emoticons) {
        emitter.onNext(emoticons)
    }

    override fun onDestroy() {
        super.onDestroy()
        xmpp.leaveChatRoom(viewModel.listenerMessage)
        chatDatabase.historyChatDao().deleteHistoryChat()
    }
}