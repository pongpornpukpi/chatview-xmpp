package com.pongporn.chatview.utils.EmoticonLiveReaction

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.util.DisplayMetrics
import android.app.Activity
import android.graphics.*
import com.pongporn.chatview.R
import android.graphics.Bitmap
import android.widget.LinearLayout
import com.airbnb.lottie.LottieAnimationView
import com.pongporn.chatview.utils.drawableToBitmap
import java.util.*

class EmoticonsView : View {

    private var mPaint: Paint? = null
    private var mAnimPath: Path? = null
    private var mMatrix: Matrix? = null
    private var mLike48: Bitmap? = null
    private var mLove48: Bitmap? = null
    private var mWow48: Bitmap? = null
    private var mSad48: Bitmap? = null
    private var mAngry48: Bitmap? = null

    private var mLiveEmoticons = arrayListOf<LiveEmoticon>()
    private val X_CORDINATE_STEP = 8
    private val X_CORDINATE_OFFSET = 150
    private val X_CORDINATE_RANGE = 200
    private val Y_CORDINATE_STEP = 8
    private val Y_CORDINATE_OFFSET = 100
    private val Y_CORDINATE_RANGE = 200
    private var mScreenWidth: Int = 0
    private var mScreenHeight: Int = 0
    private lateinit var activity: Activity

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    fun initView(activity: Activity) {

        val displayMetrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
        mScreenWidth = displayMetrics.widthPixels
        mScreenHeight = displayMetrics.heightPixels
        mPaint = Paint()

        this.activity = activity

        mAnimPath = Path()
        mMatrix = Matrix()

        val likeDrawable = resources.getDrawable(R.drawable.ic_like)
        val loveDrawable = resources.getDrawable(R.drawable.ic_love)
        val wowDrawable = resources.getDrawable(R.drawable.ic_wow)
        val sadDrawable = resources.getDrawable(R.drawable.ic_sad)
        val angryDrawable = resources.getDrawable(R.drawable.ic_angry)

        mLike48 = likeDrawable.drawableToBitmap()
        mLove48 = loveDrawable.drawableToBitmap()
        mWow48 = wowDrawable.drawableToBitmap()
        mSad48 = sadDrawable.drawableToBitmap()
        mAngry48 = angryDrawable.drawableToBitmap()
    }

    override fun onDraw(canvas: Canvas) {
        mAnimPath?.let { mPaint?.let { it1 -> canvas.drawPath(it, it1) } }
        drawAllLiveEmoticons(canvas)
    }

    private fun drawAllLiveEmoticons(canvas: Canvas) {
        val iterator = mLiveEmoticons.listIterator()
        while (iterator.hasNext()) {
            val `object` = iterator.next()

            val xCoordinate = `object`.getxCordinate() + 600
            val yCoordinate = `object`.getyCordinate() - Y_CORDINATE_STEP
            `object`.setyCordinate(yCoordinate)
            if (yCoordinate > 0) {
                mMatrix?.reset()
                mMatrix?.postTranslate(xCoordinate.toFloat(), yCoordinate.toFloat())
                resizeImageSizeBasedOnXCoordinates(canvas, `object`)
                invalidate()
            } else {
                iterator.remove()
            }
        }
    }

    private fun resizeImageSizeBasedOnXCoordinates(canvas: Canvas, liveEmoticon: LiveEmoticon?) {
        if (liveEmoticon == null) {
            return
        }

        val yCoordinate = liveEmoticon.getyCordinate()
        var bitMap48: Bitmap? = null
        var scaled: Bitmap? = null

        val emoticons = liveEmoticon.getEmoticons() ?: return

        bitMap48 = when (emoticons) {
            Emoticons.LIKE -> mLike48
            Emoticons.LOVE -> mLove48
            Emoticons.WOW -> mWow48
            Emoticons.SAD -> mSad48
            Emoticons.ANGRY -> mAngry48
        }

        if (yCoordinate < 750) {
            scaled = Bitmap.createScaledBitmap(
                bitMap48!!,
                bitMap48.width / 2,
                bitMap48.height / 2,
                false
            )
            canvas.drawBitmap(scaled, mMatrix!!, null)
        } else if (yCoordinate == 750) {

        } else {
            val likeDrawable = resources.getDrawable(R.drawable.ic_profile)
            bitMap48 = likeDrawable.drawableToBitmap()

            scaled = Bitmap.createScaledBitmap(
                bitMap48!!,
                bitMap48.width / 2,
                bitMap48.height / 2,
                false
            )
            canvas.drawBitmap(scaled, mMatrix!!, null)
        }
    }

    fun addView(emoticons: Emoticons) {
        val startXCoordinate = Random().nextInt(X_CORDINATE_RANGE) + X_CORDINATE_OFFSET
        val startYCoordinate = mScreenHeight - 1000
        val liveEmoticon = LiveEmoticon()
        liveEmoticon.LiveEmoticon(emoticons, startXCoordinate, startYCoordinate)
        mLiveEmoticons.add(liveEmoticon)
        invalidate()
    }

}