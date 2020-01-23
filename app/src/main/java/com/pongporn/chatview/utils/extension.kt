package com.pongporn.chatview.utils

import android.text.format.DateFormat
import org.jivesoftware.smack.packet.Message
import org.jivesoftware.smackx.delay.packet.DelayInformation
import java.sql.Time
import java.util.concurrent.TimeUnit

fun Int.convertMillisToDataTime() : String {
    val milliseconds: Int = this
    val second = milliseconds / 1000
    val minute = second / 60
    val hour = minute / 60
    val str = "$hour:$minute:$second"
    return str
}

fun Int.convertMillisToSecond() : Int {
    val milliseconds: Int = this
    return TimeUnit.MILLISECONDS.toSeconds(milliseconds.toLong()).toInt()
}

fun Int.convertSecondToMinutes() : Int {
    val seconds : Int = this
    return TimeUnit.SECONDS.toMinutes(seconds.toLong()).toInt()
}

fun Long.convertTimeMilliToString(): String {
    val dateInMilliseconds = this
    val dateFormat = "HH:mm"
    return DateFormat.format(dateFormat, dateInMilliseconds)
        .toString()
}

fun Message.getChatTimestamp(): String {
    val msg = this
    val ts: Long
    var timestamp: DelayInformation? = msg.getExtension("delay", "urn:xmpp:delay")
    if (timestamp == null)
        timestamp = msg.getExtension("x", "jabber:x:delay")

    ts = timestamp?.stamp?.time ?: System.currentTimeMillis()

    return ts.convertTimeMilliToString()
}