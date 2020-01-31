package com.pongporn.chatview.module.chat

import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayerSupportFragment
import com.pongporn.chatview.R
import com.pongporn.chatview.database.ChatDatabase
import com.pongporn.chatview.http.response.VideoDataResponseModel
import com.pongporn.chatview.model.ChatMessageModel
import com.pongporn.chatview.module.userlist.UserListModel
import com.pongporn.chatview.utils.XMPP
import com.pongporn.chatview.utils.convertMillisToDataTime
import com.pongporn.chatview.utils.convertMillisToSecond
import com.pongporn.chatview.viewmodel.ChatViewModel
import kotlinx.android.synthetic.main.activity_chat_view.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_view)
        userList = intent.getParcelableExtra<UserListModel>(USER_NAME)
        initObserver()
        initListener()
        initView()
        if (userList?.isGroup == true) {
            xmpp.onCreateMultiChatGroupRoom(userList?.name)
            xmpp.onJoinMultiChatGroupRoom()
            xmpp.initMam()
            if (xmpp.isJoined() == true) {
                viewModel.addlistenerMulti()
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

        btn_post.setOnClickListener {
            xmpp.multiChatSendMessage(et_comment.text.toString())
            et_comment.setText("")
            xmpp.hideSoftKeyboard(this@ChatViewActivity)
            ln_chat_view.visibility = View.INVISIBLE
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

    override fun onDestroy() {
        super.onDestroy()
        xmpp.leaveChatRoom(viewModel.listenerMessage)
        chatDatabase.historyChatDao().deleteHistoryChat()
    }
}