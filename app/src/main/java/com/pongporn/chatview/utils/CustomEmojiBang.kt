package com.pongporn.chatview.utils

import android.annotation.TargetApi
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet
import android.widget.FrameLayout
import com.pongporn.chatview.R
import kotlinx.android.synthetic.main.item_emoji.view.*

class CustomEmojiBang : FrameLayout {

    constructor(context: Context) : super(context) {
        initView(null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView(attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr
    ) {
        initView(attrs)
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(
        context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleAttr) {
        initView(attrs)
    }

    private fun initView(attrs: AttributeSet?) {
        inflate(context, R.layout.item_emoji, this)
        setupStyleable(attrs)
        setupView()
        initListener()
    }

    private fun initListener() {

    }

    private fun setupStyleable(attrs: AttributeSet?) {

    }

    private fun setupView() {

    }

    fun setImageBitmap(bitmap : Bitmap?) {
        img_emoji_popup.setImageBitmap(bitmap)

    }

    fun setLnBackground(resId : Int) {
        ln_item_emoji.setBackgroundColor(resId)
    }

    fun setBackGround(resId : Int) {
        img_emoji_popup.setBackgroundResource(resId)
    }

    fun setColor(colorStartCircle : Int ,colorEndCircle : Int,colorDot : IntArray) {
        small_bang.setCircleStartColor(colorStartCircle)
        small_bang.setCircleEndColor(colorEndCircle)
        small_bang.setDotColors(colorDot)
    }

}