package com.pongporn.chatview.utils.EmoticonLiveReaction

class LiveEmoticon {

    private var emoticons: Emoticons? = null
    private var xCordinate: Int = 0
    private var yCordinate: Int = 0

    fun LiveEmoticon(emoticons: Emoticons, xCordinate: Int, yCordinate: Int) {
        this.emoticons = emoticons
        this.xCordinate = xCordinate
        this.yCordinate = yCordinate
    }

    fun getEmoticons(): Emoticons? {
        return emoticons
    }

    fun setEmoticons(emoticons: Emoticons) {
        this.emoticons = emoticons
    }

    fun getxCordinate(): Int {
        return xCordinate
    }

    fun setxCordinate(xCordinate: Int) {
        this.xCordinate = xCordinate
    }

    fun getyCordinate(): Int {
        return yCordinate
    }

    fun setyCordinate(yCordinate: Int) {
        this.yCordinate = yCordinate
    }
}