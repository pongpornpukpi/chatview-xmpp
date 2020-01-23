package com.pongporn.chatview.utils

import android.text.format.DateFormat
import org.jivesoftware.smack.packet.Message
import org.jivesoftware.smackx.delay.packet.DelayInformation
import java.util.concurrent.TimeUnit

fun Int.convertMillisToMinutesAndSecond() : ArrayList<Int> {
    val milliseconds: Int = this
    val minutes = milliseconds / 1000 / 60
    val seconds = milliseconds / 1000 % 60
    println("$milliseconds Milliseconds = $minutes minutes and $seconds seconds.")
    return arrayListOf(minutes,seconds)
}

fun Int.convertMillisToSecond() : Int {
    val milliseconds: Int = this
    return TimeUnit.MILLISECONDS.toSeconds(milliseconds.toLong()).toInt()
}

fun Int.convertMillisToMinutes() : Int {
    val milliseconds: Int = this
    return TimeUnit.MILLISECONDS.toMinutes(milliseconds.toLong()).toInt()
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