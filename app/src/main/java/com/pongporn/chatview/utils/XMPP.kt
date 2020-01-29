package com.pongporn.chatview.utils

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.jivesoftware.smack.ConnectionConfiguration
import org.jivesoftware.smack.MessageListener
import org.jivesoftware.smack.XMPPConnection
import org.jivesoftware.smack.tcp.XMPPTCPConnection
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration
import org.jivesoftware.smackx.muc.MultiUserChat
import org.jivesoftware.smackx.muc.MultiUserChatManager
import org.jxmpp.jid.DomainBareJid
import org.jxmpp.jid.EntityBareJid
import org.jxmpp.jid.impl.JidCreate
import org.jxmpp.jid.parts.Resourcepart
import java.lang.Exception
import java.net.InetAddress
import javax.net.ssl.HostnameVerifier
import org.jivesoftware.smack.SmackException
import org.jivesoftware.smack.chat2.ChatManager
import org.jivesoftware.smack.packet.Message
import org.jivesoftware.smackx.mam.MamManager
import org.jivesoftware.smackx.muc.MucEnterConfiguration
import org.jxmpp.jid.util.JidUtil
import org.jxmpp.stringprep.XmppStringprepException
import java.util.*

class XMPP {

    lateinit var config: XMPPTCPConnectionConfiguration
    lateinit var addr: InetAddress
    lateinit var serviceName: DomainBareJid
    lateinit var verifier: HostnameVerifier
    lateinit var connection: XMPPConnection
    private var mContext: Context? = null

    var xmppName: String? = ""
    var multiUserManager: MultiUserChatManager? = null
    var multiUserJid: EntityBareJid? = null
    var multiUserChat: MultiUserChat? = null
    var nickname: Resourcepart? = null
    var listenerMessage: MessageListener? = null
    var mucEnterConfiguration : MucEnterConfiguration? = null
    var mamManager : MamManager? = null

    fun XMPPConnecttion(
        name: String?,
        password: String?,
        context: Context?
    ) {
        xmppName = name
        mContext = context
        addr = InetAddress.getByName("13.76.246.6")
        serviceName = JidCreate.domainBareFrom("natchatserver")
        verifier = HostnameVerifier { p0, p1 ->
            return@HostnameVerifier false
        }

        config = XMPPTCPConnectionConfiguration.builder()
            .setUsernameAndPassword(name, password)
            .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
            .setXmppDomain(serviceName)
            .setHostnameVerifier(verifier)
            .setHostAddress(addr)
            .setPort(5222)
            .build()
        connection = XMPPTCPConnection(config)
    }

    fun XMPPConnect() {
        try {
            (connection as XMPPTCPConnection).connect()
            Log.d("app connect", "${isConnect()}")
        } catch (e: Exception) {
            Log.d("app connect", e.toString())
        }
    }

    fun XMPPLogin() {
        try {
            (connection as XMPPTCPConnection).login()
            Log.d("app login", "${isAuthenticate()}")
        } catch (e: Exception) {
            Log.d("app login", e.toString())
        }
    }

    fun initMam() {
        // check the connection object
        if (connection != null) {
            //get the instance of MamManager
            mamManager = MamManager.getInstanceFor(multiUserChat)
            println("mamManager : ${mamManager?.isSupported}")
        }
    }

    fun logOut() {
        try {
            (connection as? XMPPTCPConnection)?.disconnect()
            Toast.makeText(mContext, "app logOut : logOut Success.", Toast.LENGTH_SHORT).show()
            Log.d("app logOut", isConnect().toString())
        } catch (e: Exception) {
            Toast.makeText(mContext, "app logOut : logOut Error.", Toast.LENGTH_SHORT).show()
            Log.d("app logOut", e.toString())
        }
    }

    fun multiChatSendMessage(message: String) {
        Log.d("app ", "Sending message to :$message")
        try {
            multiUserChat?.sendMessage(message)
        } catch (e: Exception) {
            Toast.makeText(mContext, "app Sending : Sending Error.", Toast.LENGTH_SHORT).show()
            Log.d("app", "Sending $e")
        }
    }

    fun onCreateMultiChatGroupRoom(name: String?) {
        try {
            multiUserJid = JidCreate.entityBareFrom("$name@conference.natchatserver")
            nickname = Resourcepart.from(xmppName)
            multiUserManager = MultiUserChatManager.getInstanceFor(connection)
            multiUserChat = multiUserManager?.getMultiUserChat(multiUserJid)
            mucEnterConfiguration = multiUserChat?.getEnterConfigurationBuilder(nickname)
                ?.requestNoHistory()
                ?.build()

            val owners = JidUtil.jidSetFrom(arrayOf("kia.puk@natchatserver", "nonnyzcsrt@enatchatserver"))

            multiUserChat?.create(nickname)
//                ?.configFormManager
//                ?.setRoomOwners(owners)
//                ?.submitConfigurationForm()
                ?.makeInstant()
        } catch (e: Exception) {
            Toast.makeText(mContext, "app create : create Error.", Toast.LENGTH_SHORT).show()
            Log.d("app create", e.toString())
        }

    }

    fun onJoinMultiChatGroupRoom() {
        try {
            multiUserChat?.join(mucEnterConfiguration)
            if (isJoined() == true) {
                Toast.makeText(mContext, "app Join : Join Room Success.", Toast.LENGTH_SHORT).show()
                Log.d("app Join", "Join Room Success.")
            }
        } catch (e: Exception) {
            Toast.makeText(mContext, "app Join : Join Error.", Toast.LENGTH_SHORT).show()
            Log.d("app Join", e.toString())
        }

    }

    fun leaveChatRoom(listenerMessage : MessageListener?) {
        try {
            if (isJoined() == true) multiUserChat?.leave()
            val sss = multiUserChat?.removeMessageListener(listenerMessage)
            Toast.makeText(mContext, "app leave : leave Room Success.", Toast.LENGTH_SHORT).show()
            Log.d("app leave", sss.toString())
            Log.d("app leave", multiUserChat?.isJoined.toString())
        } catch (e: Exception) {
            Toast.makeText(mContext, "app leave : leave Error.", Toast.LENGTH_SHORT).show()
            Log.d("app leave", e.toString())
        }
    }

    fun isConnect(): Boolean {
        return connection.isConnected
    }

    fun isAuthenticate(): Boolean {
        return connection.isAuthenticated
    }

    fun isJoined(): Boolean? {
        return multiUserChat?.isJoined
    }

}