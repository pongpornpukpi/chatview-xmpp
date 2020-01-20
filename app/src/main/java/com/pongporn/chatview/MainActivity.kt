package com.pongporn.chatview

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.pongporn.chatview.userlist.UserListActivity
import com.pongporn.chatview.utils.XMPP
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.ext.android.inject


class MainActivity : AppCompatActivity() {

    private var name: String? = ""
    private var password: String? = ""
    private val xmpp: XMPP by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initClick()
    }

    private fun initClick() {
        button.setOnClickListener {
            name = editText.text.toString()
            password = editText2.text.toString()
            val task = MyLoginTask(this, xmpp)
            task.setUsernameAndPassword(name, password)
            task.execute()
        }
    }

    class MyLoginTask(
        private var context: Context,
        private val xmpp: XMPP
    ) : AsyncTask<String, String, String>() {

        private var name: String? = ""
        private var password: String? = ""

        fun setUsernameAndPassword(
            name: String?,
            password: String?
        ) {
            this.name = name
            this.password = password
        }

        override fun doInBackground(vararg p0: String?): String {
            xmpp.XMPPConnecttion(name, password)
            xmpp.XMPPConnect()
            xmpp.XMPPLogin()

            if (xmpp.isAuthenticate()) {
                val intent = Intent(context, UserListActivity::class.java)
                intent.putExtra(UserListActivity.NAME_USER, name)
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
