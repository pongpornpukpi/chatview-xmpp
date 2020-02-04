package com.pongporn.chatview.utils.EmoticonsExcample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.pongporn.chatview.R
import android.view.ViewGroup
import android.view.animation.Animation

class MainEmoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_emo)
        emoji_one()
        emoji_two()
        emoji_three()
    }

    fun flyEmoji(resId: Int) {
        val animation = ZeroGravityAnimation()
        animation.setCount(1)
        animation.setScalingFactor(0.2f)
        animation.setOriginationDirection(Direction.BOTTOM)
        animation.setDestinationDirection(Direction.TOP)
        animation.setImage(resId)
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {

            }

            override fun onAnimationEnd(animation: Animation) {

            }

            override fun onAnimationRepeat(animation: Animation) {

            }
        }
        )

        val container = findViewById<ViewGroup>(R.id.animation_holder)
        animation.play(this, container)
    }

    fun emoji_one() {
        // You can change the number of emojis that will be flying on screen
        for (i in 0..4) {
            flyEmoji(R.drawable.ic_like)
        }
    }
    // You can change the number of emojis that will be flying on screen

    fun emoji_two() {
        for (i in 0..4) {
            flyEmoji(R.drawable.ic_love)
        }

    }
    // You can change the number of emojis that will be flying on screen

    fun emoji_three() {
        for (i in 0..4) {
            flyEmoji(R.drawable.ic_wow)
        }

    }


    // This method will be used if You want to fly your Emois Over any view

//    public void flyObject(final int resId, final int duration, final Direction from, final Direction to, final float scale) {
//
//        ZeroGravityAnimation animation = new ZeroGravityAnimation();
//        animation.setCount(1);
//        animation.setScalingFactor(scale);
//        animation.setOriginationDirection(from);
//        animation.setDestinationDirection(to);
//        animation.setImage(resId);
//        animation.setDuration(duration);
//        animation.setAnimationListener(new Animation.AnimationListener() {
//            @Override
//            public void onAnimationStart(Animation animation) {
//
//            }
//
//            @Override
//            public void onAnimationEnd(Animation animation) {
//
//                flyObject(resId, duration, from, to, scale);
//            }
//
//            @Override
//            public void onAnimationRepeat(Animation animation) {
//
//            }
//        });
//
//        ViewGroup container = (ViewGroup) findViewById(R.id.animation_bigger_objects_holder);
//        animation.play(this,container);
//
//    }
//

}