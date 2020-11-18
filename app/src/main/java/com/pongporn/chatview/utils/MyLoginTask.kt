package com.pongporn.chatview.utils

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import com.pongporn.chatview.module.userlist.UserListActivity

class MyLoginTask(
    private var context: Context,
    private val xmpp: XMPP
) : AsyncTask<String, String, String>() {

    private var name: String? = ""
    private var password: String? = ""
    private var nameRoom: String? = ""

    fun setUsernameAndPassword(
        name: String?,
        password: String?,
        nameRoom: String?
    ) {
        this.name = name
        this.password = password
        this.nameRoom = nameRoom
    }

    override fun doInBackground(vararg p0: String?): String {
        xmpp.XMPPConnecttion(name, password, context)
        xmpp.XMPPConnect()
        xmpp.XMPPLogin()

        return ""
    }

    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)
        if (xmpp.isAuthenticate()) {
            val intent = Intent(context, UserListActivity::class.java)
            intent.putExtra(UserListActivity.NAME_USER, name)
            intent.putExtra(UserListActivity.NAME_ROOM, nameRoom)
            context.startActivity(intent)
        }
    }

    override fun onPreExecute() {
        super.onPreExecute()
    }
}