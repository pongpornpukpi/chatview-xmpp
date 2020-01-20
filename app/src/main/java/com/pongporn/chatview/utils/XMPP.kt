package com.pongporn.chatview.utils

import android.util.Log
import com.pongporn.chatview.chat.ChatViewAdapter
import org.jivesoftware.smack.ConnectionConfiguration
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
import org.jxmpp.jid.util.JidUtil
import org.jivesoftware.smack.SmackException
import org.jivesoftware.smack.chat2.Chat
import org.jivesoftware.smack.chat2.ChatManager
import org.jivesoftware.smack.chat2.IncomingChatMessageListener
import org.jivesoftware.smack.packet.Message
import org.jxmpp.stringprep.XmppStringprepException

class XMPP {

    lateinit var config: XMPPTCPConnectionConfiguration
    lateinit var addr: InetAddress
    lateinit var serviceName: DomainBareJid
    lateinit var verifier: HostnameVerifier
    lateinit var connection: XMPPConnection
    var xmppName: String? = ""

    fun XMPPConnecttion(
        name: String?,
        password: String?
    ) {
        xmppName = name
        addr = InetAddress.getByName("13.76.246.6")
        serviceName = JidCreate.domainBareFrom("natchatserver")
        verifier = HostnameVerifier { p0, p1 ->
            return@HostnameVerifier false
        }

//      .setUsernameAndPassword("kia.puk", "123456")
//      .setUsernameAndPassword("nonnyzcsrt","04060406")

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

    fun isConnect(): Boolean {
        return connection.isConnected
    }

    fun isAuthenticate(): Boolean {
        return connection.isAuthenticated
    }

    var multiUserManager : MultiUserChatManager? = null
    var multiUserJid: EntityBareJid? = null
    var multiUserChat: MultiUserChat? = null
    var nickname: Resourcepart? = null

    fun onCreateOneOnOneChatRoom() {

    }

    fun sendMessage(body: String, toJid: String) {
        Log.d("app", "Sending message to :$toJid")
        var jid: EntityBareJid? = null
        val chatManager = ChatManager.getInstanceFor(connection)

        try {
            jid = JidCreate.entityBareFrom(toJid)
        } catch (e: XmppStringprepException) {
            e.printStackTrace()
        }

        val chat = chatManager.chatWith(jid)
        try {
            val message = Message(jid, Message.Type.chat)
            message.body = body
            chat.send(message)

        } catch (e: SmackException.NotConnectedException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

    }

    fun multiChatSendMessage(message: String) {
        Log.d("app ", "Sending message to :$message")
        try {
            multiUserChat?.sendMessage(message)
        } catch (e :Exception) {
            Log.d("app", "Sending $e")
        }
    }

    fun onCreateMultiChatGroupRoom(name: String?) {
        try {
            multiUserJid = JidCreate.entityBareFrom("$name@conference.natchatserver")
            nickname = Resourcepart.from(xmppName)
            multiUserManager = MultiUserChatManager.getInstanceFor(connection)
            multiUserChat = multiUserManager?.getMultiUserChat(multiUserJid)

            val owners = JidUtil.jidSetFrom(arrayOf("me@natchatserver", "juliet@enatchatserver"))

            multiUserChat?.create(nickname)?.configFormManager?.setRoomOwners(owners)?.submitConfigurationForm()
        } catch (e: Exception) {
            Log.d("app create", e.toString())
        }
    }

    fun isJoined(): Boolean? {
        return multiUserChat?.isJoined
    }

    fun onJoinMultiChatGroupRoom() {
        try {
            multiUserChat?.join(nickname)
            if (isJoined() == true) {
                Log.d("app Join", "Join Room Success.")
            }
        } catch (e: Exception) {
            Log.d("app Join", e.toString())
        }
    }


}