package com.pongporn.chatview.utils

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import android.widget.Toast
import co.intentservice.chatui.models.ChatMessage
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
import org.jivesoftware.smack.packet.Message
import org.jivesoftware.smackx.mam.MamManager
import org.jivesoftware.smackx.mam.element.MamElements
import org.jivesoftware.smackx.rsm.packet.RSMSet
import org.jivesoftware.smackx.xdata.FormField
import org.jivesoftware.smackx.xdata.packet.DataForm
import org.jxmpp.jid.BareJid
import org.jxmpp.stringprep.XmppStringprepException
import org.jxmpp.util.XmppStringUtils

class XMPP {

    lateinit var config: XMPPTCPConnectionConfiguration
    lateinit var addr: InetAddress
    lateinit var serviceName: DomainBareJid
    lateinit var verifier: HostnameVerifier
    lateinit var connection: XMPPConnection
    var xmppName: String? = ""
    private var mContext: Context? = null

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

    fun isConnect(): Boolean {
        return connection.isConnected
    }

    fun isAuthenticate(): Boolean {
        return connection.isAuthenticated
    }

    var multiUserManager: MultiUserChatManager? = null
    var multiUserJid: EntityBareJid? = null
    var multiUserChat: MultiUserChat? = null
    var nickname: Resourcepart? = null

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
        } catch (e: Exception) {
            Toast.makeText(mContext, "app Sending : Sending Error.", Toast.LENGTH_SHORT).show()
            Log.d("app", "Sending $e")
        }
    }

    fun getXMPPFullJID() {

    }

    fun onCreateMultiChatGroupRoom(name: String?) {
        try {
            multiUserJid = JidCreate.entityBareFrom("$name@conference.natchatserver")
            nickname = Resourcepart.from(xmppName)
            multiUserManager = MultiUserChatManager.getInstanceFor(connection)
            multiUserChat = multiUserManager?.getMultiUserChat(multiUserJid)

            val owners =
                JidUtil.jidSetFrom(arrayOf("kia.puk@natchatserver", "nonnyzcsrt@enatchatserver"))

            multiUserChat?.create(nickname)?.makeInstant()
//                ?.configFormManager
//                ?.setRoomOwners(owners)
//                ?.submitConfigurationForm()
        } catch (e: Exception) {
            Toast.makeText(mContext, "app create : create Error.", Toast.LENGTH_SHORT).show()
            Log.d("app create", e.toString())
        }

    }

    fun isJoined(): Boolean? {
        return multiUserChat?.isJoined
    }

    fun leaveChatRoom() {
        try {
            if (isJoined() == true) multiUserChat?.leave()
            Toast.makeText(mContext, "app leave : leave Room Success.", Toast.LENGTH_SHORT).show()
            Log.d("app leave", multiUserChat?.isJoined.toString())
        } catch (e: Exception) {
            Toast.makeText(mContext, "app leave : leave Error.", Toast.LENGTH_SHORT).show()
            Log.d("app leave", e.toString())
        }
    }

    fun onJoinMultiChatGroupRoom() {
        try {
            multiUserChat?.join(nickname)
            if (isJoined() == true) {
                Toast.makeText(mContext, "app Join : Join Room Success.", Toast.LENGTH_SHORT).show()
                Log.d("app Join", "Join Room Success.")
            }
        } catch (e: Exception) {
            Toast.makeText(mContext, "app Join : Join Error.", Toast.LENGTH_SHORT).show()
            Log.d("app Join", e.toString())
        }

    }

    fun getChatHistoryWithJID(jid: String, maxResults: Int): List<ChatMessage> {
        val chatMessageList: ArrayList<ChatMessage> = arrayListOf()
        val mamQueryResult: MamManager.MamQueryResult? = getArchivedMessages(jid, maxResults)
        val userSendTo: String = XmppStringUtils.parseBareJid(jid)
        val forwarded = mamQueryResult?.forwardedMessages

        try {
            if (mamQueryResult != null && userSendTo != null) {
                for (index in 0 until forwarded!!.size) {
                    if (forwarded.get(index).forwardedStanza is Message) {
                        val msg: Message = forwarded.get(index) as Message
                        Log.d(TAG, "onCreate: $msg")
                        Log.d(
                            TAG,
                            "processStanza: " + msg.from + " Say：" + msg.body + " String length：" + (msg.body != null ?: msg.body.length ?: "")
                        )
                        var chatMessage =
                            if (XmppStringUtils.parseBareJid(msg.from.toString()) == userSendTo) {
                                ChatMessage(
                                    msg.body,
                                    forwarded.get(index).delayInformation.stamp.time,
                                    ChatMessage.Type.RECEIVED
                                )
                            } else {
                                ChatMessage(
                                    msg.body,
                                    forwarded.get(index).delayInformation.stamp.time,
                                    ChatMessage.Type.SENT
                                )
                            }
                        chatMessageList.add(chatMessage)
                    }
                }
            } else {
                return chatMessageList
            }
            return chatMessageList
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return chatMessageList
    }

    private fun getArchivedMessages(jid: String, maxResult: Int): MamManager.MamQueryResult? {
        val mamManager = MamManager.getInstanceFor(connection)
        try {
            val form = DataForm(DataForm.Type.submit)
            val field = FormField(FormField.FORM_TYPE)
            field.type = FormField.Type.hidden
            field.addValue(MamElements.NAMESPACE)
            form.addField(field)

            val formField = FormField("with")
            formField.addValue(jid)
            form.addField(formField)

            val rsmSet = RSMSet(maxResult, "", RSMSet.PageDirection.before)
            val mamQueryResult = mamManager.page(form, rsmSet)

            return mamQueryResult
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("app history", "$e")
        }
        return null
    }

    fun getHistory(): Message? {
        try {
            val manager: MamManager = MamManager.getInstanceFor(connection)
            val jid1 = JidCreate.bareFrom("kia.puk@natchatserver/Android")
            val jid2 = JidCreate.bareFrom("nonnyzcsrt@enatchatserver/Android")
            val jidList = listOf<BareJid>(jid1, jid2)
            for (index in jidList.indices) {
                val mamQueryArgs = MamManager.MamQueryArgs.builder()
                    .setResultPageSize(1).limitResultsToJid(jidList.get(index))
                    .queryLastPage().build()
                val mamQuery: MamManager.MamQuery = manager.queryArchive(mamQueryArgs)

            }
//            val r  : MamManager.MamQueryResult = manager.mostRecentPage(multiUserJid, 100)
//            if (r.forwardedMessages.size >= 1) //printing first of them
//            {
//                val message : Message = r.forwardedMessages.get(0).forwardedStanza as Message
//                Log.d("mam", "message received" + message.body)
//                return message
//            }
        } catch (e: Exception) {
            Log.d("app error history", e.toString())
        }
        return null
    }

}