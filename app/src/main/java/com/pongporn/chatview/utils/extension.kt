package com.pongporn.chatview.utils

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