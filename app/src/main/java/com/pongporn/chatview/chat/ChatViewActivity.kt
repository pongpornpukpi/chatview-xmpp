package com.pongporn.chatview.chat

import android.os.Bundle
import android.os.Handler
import android.util.Log
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
import com.pongporn.chatview.database.entity.HistoryChatEntity
import com.pongporn.chatview.http.response.VideoDataResponseModel
import com.pongporn.chatview.userlist.UserListModel
import com.pongporn.chatview.utils.XMPP
import com.pongporn.chatview.utils.convertMillisToMinutes
import com.pongporn.chatview.utils.convertMillisToMinutesAndSecond
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
    private var chatList = mutableListOf<HistoryChatEntity>()
    private var newPositionMillis: Int = 0

    lateinit var youTubePlayerInit: YouTubePlayer.OnInitializedListener
    lateinit var youtubePlayerFillScreen: YouTubePlayer.OnFullscreenListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_view)
        userList = intent.getParcelableExtra<UserListModel>(USER_NAME)
        if (userList?.isGroup == true) {
            xmpp.onCreateMultiChatGroupRoom(userList?.name)
            xmpp.onJoinMultiChatGroupRoom()
            if (xmpp.isJoined() == true) {
                viewModel.addlistenerMulti()
            }
        } else {
            viewModel.addlistenerOneOnOne()
        }
        initObserver()
        initListener()
        initView()
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
//                        this@ChatViewActivity.newPositionMillis = newPositionMillis
                    }

                    override fun onBuffering(isBuffering: Boolean) {
                        Log.d("youtube", "onBuffering $isBuffering")
                    }

                    override fun onPlaying() {
                        Log.d("youtube", "onPlaying")
                        newPositionMillis = youTubePlayer.currentTimeMillis.convertMillisToSecond()
                        val duraMinute =
                            youTubePlayer.durationMillis.convertMillisToMinutesAndSecond()
                        val currentMinute =
                            youTubePlayer.currentTimeMillis.convertMillisToMinutesAndSecond()
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
                Snackbar.make(et_comment, "YouTube!! Init Failure. $error", Snackbar.LENGTH_SHORT)
                    .show()
            }
        }

        et_comment.setOnClickListener {

        }

        btn_post.setOnClickListener {
            if (userList?.isGroup == true) {
                chatDatabase.historyChatDao().deleteHistoryChat().run {
                    xmpp.multiChatSendMessage(et_comment.text.toString())
                }
            } else {
                xmpp.sendMessage(et_comment.text.toString(), "${userList?.name}@natchatserver")
//                chatList.add("${userList?.name} : ${et_comment.text}")
                chatAdapter.clearList()
                chatAdapter.addlist(chatList)
            }
        }
    }

    private fun initObserver() {
        viewModel.getmessage().observe(this, Observer<String> {
            chatDatabase.historyChatDao().getHisrotyChat().apply {
                println("ListMessage : $this")
                chatList.addAll(this)
                chatAdapter.clearList()
                chatAdapter.addlist(chatList)
            }
        })
        viewModel.getVideoData().observe(this, Observer<VideoDataResponseModel> {
            println("VideoDataResponse : $it")
        })

        viewModel.getVideoDataRequest(id = VIDEO_ID, key = YOUTUBE_API_KEY, part = "snippet")
    }

    private fun initView() {
        recyclerview_chat.apply {
            layoutManager = LinearLayoutManager(this@ChatViewActivity, RecyclerView.VERTICAL, false)
            adapter = chatAdapter
        }

        title = userList?.name

        val frag =
            supportFragmentManager.findFragmentById(R.id.youtube_fragment) as YouTubePlayerSupportFragment?
        frag?.initialize(YOUTUBE_API_KEY, youTubePlayerInit)
    }

    override fun onDestroy() {
        super.onDestroy()
        xmpp.leaveChatRoom(viewModel.listenerMessage)
    }
}