package com.pongporn.chatview.utils

import android.text.format.DateFormat
import org.jivesoftware.smack.packet.Message
import org.jivesoftware.smackx.delay.packet.DelayInformation
import java.util.concurrent.TimeUnit
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable



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

fun Drawable.drawableToBitmap(): Bitmap? {
    var bitmap: Bitmap? = null
    val drawable = this
    if (drawable is BitmapDrawable) {
        if (drawable.bitmap != null) {
            return drawable.bitmap
        }
    }

    if (drawable.intrinsicWidth <= 0 || drawable.intrinsicHeight <= 0) {
        bitmap = Bitmap.createBitmap(
            1,
            1,
            Bitmap.Config.ARGB_8888
        ) // Single color bitmap will be created of 1x1 pixel
    } else {
        bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
    }

    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)
    return bitmap
}