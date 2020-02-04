package com.pongporn.chatview.utils.EmoticonLiveReaction

import android.view.animation.Animation

class CustomAnimationListener : Animation.AnimationListener {
    override fun onAnimationRepeat(p0: Animation?) {
        System.out.println("on Animation start")
    }

    override fun onAnimationEnd(p0: Animation?) {
        System.out.println("on Animation End")
    }

    override fun onAnimationStart(p0: Animation?) {

    }

}