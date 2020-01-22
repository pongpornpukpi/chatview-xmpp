package com.pongporn.chatview

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import com.pongporn.chatview.userlist.UserListActivity
import com.pongporn.chatview.utils.XMPP
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.ext.android.inject


class MainActivity : AppCompatActivity() {

    private var name: String? = ""
    private var password: String? = ""
    private var nameRoom : String? = ""
    private val xmpp: XMPP by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initClick()
    }

    private fun initClick() {
        button.setOnClickListener {
            if (editText.text.toString().isNotEmpty() && editText.text.contains("@")) {
                name = editText.text.toString().split("@")[0]
                password = editText2.text.toString()
                nameRoom = editText.text.toString().split("@")[1]
                val task = MyLoginTask(this, xmpp)
                task.setUsernameAndPassword(name, password,nameRoom)
                Snackbar.make(button,"app login : wait to login...",Snackbar.LENGTH_SHORT).show()
                Log.d("app login ",": wait to login...")
                task.execute()
            }
        }
    }

    class MyLoginTask(
        private var context: Context,
        private val xmpp: XMPP
    ) : AsyncTask<String, String, String>() {

        private var name: String? = ""
        private var password: String? = ""
        private var nameRoom : String? = ""

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
            xmpp.XMPPConnecttion(name, password,context)
            xmpp.XMPPConnect()
            xmpp.XMPPLogin()

            if (xmpp.isAuthenticate()) {
                val intent = Intent(context, UserListActivity::class.java)
                intent.putExtra(UserListActivity.NAME_USER, name)
                intent.putExtra(UserListActivity.NAME_ROOM,nameRoom)
                context.startActivity(intent)
            }

            return ""
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)

        }

        override fun onPreExecute() {
            super.onPreExecute()
        }
    }
}
