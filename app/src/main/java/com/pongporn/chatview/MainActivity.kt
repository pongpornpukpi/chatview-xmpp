package com.pongporn.chatview

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import com.pongporn.chatview.application.ChatApplication
import com.pongporn.chatview.module.chat.ChatViewActivity
import com.pongporn.chatview.module.userlist.UserListActivity
import com.pongporn.chatview.utils.PreferenceUtils
import com.pongporn.chatview.utils.XMPP
import kotlinx.android.synthetic.main.activity_main.*
import net.openid.appauth.*
import org.koin.android.ext.android.inject


class MainActivity : AppCompatActivity() {

    private var name: String? = ""
    private var password: String? = ""
    private var nameRoom: String? = ""
    private val xmpp: XMPP by inject()
    private val USED_INTENT = "USED_INTENT"
    private val SHARED_PREFERENCES_NAME = "AuthStatePreference"
    private val AUTH_STATE = "AUTH_STATE"
    val preferenceUtils: PreferenceUtils by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initClick()
    }

    override fun onStart() {
        super.onStart()
        checkIntent(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        checkIntent(intent)
    }

    private fun checkIntent(@Nullable intent: Intent?) {
        if (intent != null) {
            val action = intent.action
            when (action) {
                "com.pongporn.chatview.HANDLE_AUTHORIZATION_RESPONSE" -> if (!intent.hasExtra(
                        USED_INTENT
                    )
                ) {
                    handleAuthorizationResponse(intent)
                    intent.putExtra(USED_INTENT, true)
                }
                else -> {
                }
            }
        }
    }

    private fun handleAuthorizationResponse(intent: Intent) {
        // code from the step 'Handle the Authorization Response' goes here.
        val response = AuthorizationResponse.fromIntent(intent)
        val error = AuthorizationException.fromIntent(intent)
        val authState = AuthState(response, error)

        if (response != null) {
            Log.i(
                ChatApplication().LOG_TAG,
                String.format("Handled Authorization Response %s ", authState.toJsonString())
            )
            val service = AuthorizationService(this)
            service.performTokenRequest(
                response.createTokenExchangeRequest()
            ) { tokenResponse, exception ->
                if (exception != null) {
                    Log.w(ChatApplication().LOG_TAG, "Token Exchange failed", exception)
                } else {
                    if (tokenResponse != null) {
                        authState.update(tokenResponse, exception)
                        persistAuthState(authState)
                        Log.i(
                            ChatApplication().LOG_TAG,
                            String.format(
                                "Token Response [ Access Token: %s, ID Token: %s ]",
                                tokenResponse.accessToken,
                                tokenResponse.idToken
                            )
                        )
                    }
                }
            }
        }
    }

    private fun persistAuthState(@NonNull authState: AuthState) {
        preferenceUtils.ACCESS_TOKEN = authState.accessToken!!
        preferenceUtils.AUTH_STATE = authState.toJsonString()
        preferenceUtils.isGoogleLogin = true
        //    enablePostAuthorizationFlows();
//                    fast pass Kia
        val intent = Intent(this@MainActivity, ChatViewActivity::class.java)
        intent.putExtra(UserListActivity.NAME_USER, name)
        intent.putExtra(UserListActivity.NAME_ROOM, nameRoom)
        startActivity(intent)
    }

    private fun initClick() {
        button.setOnClickListener {

            if (!preferenceUtils.isGoogleLogin) {
                val serviceConfiguration =
                    AuthorizationServiceConfiguration(
                        Uri.parse("https://accounts.google.com/o/oauth2/v2/auth") /* auth endpoint */,
                        Uri.parse("https://www.googleapis.com/oauth2/v4/token") /* token endpoint */
                    )
                val clientId =
                    "694211781230-dp7su021ospdighpbbbgdtc8s516u6ob.apps.googleusercontent.com"
                val redirectUri =
                    Uri.parse("com.pongporn.chatview:/oauth2callback")
                val builder = AuthorizationRequest.Builder(
                    serviceConfiguration,
                    clientId,
                    AuthorizationRequest.RESPONSE_TYPE_CODE,
                    redirectUri
                )
                builder.setScopes("https://www.googleapis.com/auth/youtube")
                val request = builder.build()
                val authorizationService = AuthorizationService(this)
                val action = "com.pongporn.chatview.HANDLE_AUTHORIZATION_RESPONSE"
                val postAuthorizationIntent = Intent(action)
                val pendingIntent = PendingIntent.getActivity(
                    this,
                    request.hashCode(),
                    postAuthorizationIntent,
                    0
                )
                authorizationService.performAuthorizationRequest(request, pendingIntent)
            } else {
//                          fast pass Kia
                val intent = Intent(this@MainActivity, ChatViewActivity::class.java)
                intent.putExtra(UserListActivity.NAME_USER, name)
                intent.putExtra(UserListActivity.NAME_ROOM, nameRoom)
                startActivity(intent)
            }

//            if (editText.text.toString().isNotEmpty() && editText.text.contains("@")) {
//                name = editText.text.toString().split("@")[0]
//                password = editText2.text.toString()
//                nameRoom = editText.text.toString().split("@")[1]
//                val task = MyLoginTask(this, xmpp)
//                task.setUsernameAndPassword(name, password,nameRoom)
//                Snackbar.make(button,"app login : wait to login...",Snackbar.LENGTH_SHORT).show()
//                Log.d("app login ",": wait to login...")
//                task.execute()
//            }
        }

//        small_main.setOnClickListener {
//            if (small_main.isSelected) {
//                small_main.isSelected = false
//            } else {
//                small_main.isSelected = true
//                small_main.likeAnimation()
//                small_main.setCircleStartColor(ContextCompat.getColor(this,R.color.colorBlue))
//                small_main.setCircleEndColor(ContextCompat.getColor(this,R.color.colorBlackBlue))
//                small_main.setDotColors(resources.getIntArray(R.array.colorDotLike))
//            }
//        }
    }

//    class MyLoginTask(
//        private var context: Context,
//        private val xmpp: XMPP
//    ) : AsyncTask<String, String, String>() {
//
//        private var name: String? = ""
//        private var password: String? = ""
//        private var nameRoom : String? = ""
//
//        fun setUsernameAndPassword(
//            name: String?,
//            password: String?,
//            nameRoom: String?
//        ) {
//            this.name = name
//            this.password = password
//            this.nameRoom = nameRoom
//        }
//
//        override fun doInBackground(vararg p0: String?): String {
//            xmpp.XMPPConnecttion(name, password,context)
//            xmpp.XMPPConnect()
//            xmpp.XMPPLogin()
//
//            return ""
//        }
//
//        override fun onPostExecute(result: String?) {
//            super.onPostExecute(result)
//            if (xmpp.isAuthenticate()) {
//                val intent = Intent(context, UserListActivity::class.java)
//                intent.putExtra(UserListActivity.NAME_USER, name)
//                intent.putExtra(UserListActivity.NAME_ROOM,nameRoom)
//                context.startActivity(intent)
//            }
//        }
//
//        override fun onPreExecute() {
//            super.onPreExecute()
//        }
//    }
}
