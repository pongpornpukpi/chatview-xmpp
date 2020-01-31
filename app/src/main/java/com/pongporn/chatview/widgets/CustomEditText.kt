package com.pongporn.chatview.widgets

import android.widget.EditText
import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.KeyEvent

class CustomEditText : EditText {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes)

    fun setHandleDismissingKeyboard(handleDismissingKeyboard: onHandleDismissingKeyboard) {
        this.handleDismissingKeyboard = handleDismissingKeyboard
    }

    private var handleDismissingKeyboard: onHandleDismissingKeyboard? = null

    interface onHandleDismissingKeyboard {
        fun dismissKeyboard()
    }

    override fun onKeyPreIme(keyCode: Int, event: KeyEvent): Boolean {
        if (event.keyCode === KeyEvent.KEYCODE_BACK && event.action === KeyEvent.ACTION_UP) {
            handleDismissingKeyboard?.dismissKeyboard()
            return true
        }
        return super.dispatchKeyEvent(event)
    }
}