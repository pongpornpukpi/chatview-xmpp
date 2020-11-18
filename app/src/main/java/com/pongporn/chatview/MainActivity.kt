package com.pongporn.chatview

import android.content.Intent
import android.net.Uri
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

//    override fun onStart() {
//        super.onStart()
//        checkIntent(intent)
//    }
//
//    override fun onNewIntent(intent: Intent?) {
//        super.onNewIntent(intent)
//        checkIntent(intent)
//    }

//    private fun checkIntent(@Nullable intent: Intent?) {
//        if (intent != null) {
//            val action = intent.action
//            when (action) {
//                "com.pongporn.chatview.HANDLE_AUTHORIZATION_RESPONSE" -> if (!intent.hasExtra(USED_INTENT))
//                {
//                    handleAuthorizationResponse(intent)
//                    intent.putExtra(USED_INTENT, true)
//                }
//                else -> {
//                }
//            }
//        }
//    }

    private fun handleAuthorizationResponse(intent: Intent) {
        // code from the step 'Handle the Authorization Response' goes here.
        val response = AuthorizationResponse.fromIntent(intent)
        val error = AuthorizationException.fromIntent(intent)
        val authState = AuthState(response, error)

        if (response != null) {
            Log.i(
                ChatApplication().LOG_TAG,
                String.format("Handled Authorization Response %s ", authState.toString())
            )
            val service = AuthorizationService(this)
            service.performTokenRequest(response.createTokenExchangeRequest()) { tokenResponse, exception ->
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
        preferenceUtils.AUTH_STATE = authState.toString()
        preferenceUtils.isGoogleLogin = true
        //    enablePostAuthorizationFlows();
        val intent = Intent(this@MainActivity, ChatViewActivity::class.java)
        intent.putExtra(UserListActivity.NAME_USER, name)
        intent.putExtra(UserListActivity.NAME_ROOM, nameRoom)
        startActivity(intent)
    }

    private fun initClick() {
        button.setOnClickListener {

            if (!preferenceUtils.isGoogleLogin) {
                val serviceConfiguration = AuthorizationServiceConfiguration(
                        Uri.parse("https://accounts.google.com/o/oauth2/v2/auth") /* auth endpoint */,
                        Uri.parse("https://www.googleapis.com/oauth2/v4/token") /* token endpoint */
                    )
                val clientId = "694211781230-dp7su021ospdighpbbbgdtc8s516u6ob.apps.googleusercontent.com"
                val redirectUri = Uri.parse("com.pongporn.chatview:/oauth2callback")
                val builder = AuthorizationRequest.Builder(
                    serviceConfiguration,
                    clientId,
                    ResponseTypeValues.CODE, // the response_type value: we want a code
                    redirectUri
                )
                builder.setScopes("https://www.googleapis.com/auth/youtube")
                val request = builder.build()
                val authorizationService = AuthorizationService(this)
                val authIntent: Intent = authorizationService.getAuthorizationRequestIntent(request)
                startActivityForResult(authIntent, 199)

//                val action = "com.pongporn.chatview.HANDLE_AUTHORIZATION_RESPONSE"
//                val postAuthorizationIntent = Intent(action)
//                val pendingIntent = PendingIntent.getActivity(
//                    this,
//                    request.hashCode(),
//                    postAuthorizationIntent,
//                    0
//                )
//                authorizationService.performAuthorizationRequest(request, pendingIntent)

            } else {
                val intent = Intent(this@MainActivity, ChatViewActivity::class.java)
                intent.putExtra(UserListActivity.NAME_USER, name)
                intent.putExtra(UserListActivity.NAME_ROOM, nameRoom)
                startActivity(intent)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 199) {
            handleAuthorizationResponse(data!!)
            // ... process the response or exception ...
        } else { // ...
        }
    }
}
