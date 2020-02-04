package com.pongporn.chatview.utils

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.text.format.DateFormat
import org.jivesoftware.smack.packet.Message
import org.jivesoftware.smackx.delay.packet.DelayInformation
import java.util.concurrent.TimeUnit
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Handler
import android.util.Log
import android.view.MotionEvent
import android.view.View


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

fun View.setOnAnimateClickListener(onClick: (View) -> Unit) {
    this.setOnAnimateClickListener(null, onClick)
}

fun View.setOnAnimateClickListener(resId: Int?, onClick: (View) -> Unit) {

    var secondaryView: View? = null

    if (resId != null) {
        secondaryView = this.findViewById(resId)
    }

    this.setOnClickListener { view ->
        Handler().postDelayed({
            onClick.invoke(view)
        }, 370)
    }

    this.setOnTouchListener { v, event ->
        Log.d("ANIMATE", event.toString())
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                v.animate().cancel()
                v.animate().scaleY(1.50f).scaleX(1.50f).setDuration(200).start()
                secondaryView?.animate()?.scaleY(1.1f)?.scaleX(1.1f)?.alpha(0.7f)?.setDuration(200)?.start()

                false
            }
            MotionEvent.ACTION_UP -> {

                val xBigScale = ObjectAnimator.ofFloat(v, "scaleX", 1.50f)
                xBigScale.setDuration(160).repeatCount = 0

                val yBigScale = ObjectAnimator.ofFloat(v, "scaleY", 1.50f)
                yBigScale.setDuration(160).repeatCount = 0

                val xSmallScale = ObjectAnimator.ofFloat(v, "scaleX", 0.685f)
                xSmallScale.setDuration(140).repeatCount = 0

                val ySmallScale = ObjectAnimator.ofFloat(v, "scaleY", 0.685f)
                ySmallScale.setDuration(140).repeatCount = 0

                val xNormalScale = ObjectAnimator.ofFloat(v, "scaleX", 1f)
                xNormalScale.setDuration(70).repeatCount = 0

                val yNormalScale = ObjectAnimator.ofFloat(v, "scaleY", 1f)
                yNormalScale.setDuration(70).repeatCount = 0

                val animateSet = AnimatorSet()
                animateSet.play(xBigScale).with(yBigScale)
                animateSet.play(xSmallScale).after(xBigScale)
                animateSet.play(ySmallScale).after(yBigScale)
                animateSet.play(xNormalScale).after(xSmallScale)
                animateSet.play(yNormalScale).after(ySmallScale)
                animateSet.start()

                secondaryView?.animate()?.scaleY(1f)?.scaleX(1f)?.alpha(1f)?.setDuration(370)?.start()

                false
            }

            MotionEvent.ACTION_CANCEL -> {
                v.animate().scaleY(1f).scaleX(1f).setDuration(200).start()
                secondaryView?.animate()?.scaleY(1f)?.scaleX(1f)?.alpha(1f)?.setDuration(370)?.start()

                true
            }

            else -> true
        }
    }
}