package com.pongporn.chatview.utils

fun Int.convertMillisToMinutesAndSecond() : ArrayList<Int> {
    val milliseconds: Int = this
    val minutes = milliseconds / 1000 / 60
    val seconds = milliseconds / 1000 % 60
    println("$milliseconds Milliseconds = $minutes minutes and $seconds seconds.")
    return arrayListOf(minutes,seconds)
}
