package com.pongporn.chatview.utils.EmoticonLiveReaction

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.util.DisplayMetrics
import android.app.Activity
import android.graphics.*
import com.pongporn.chatview.R
import android.graphics.Bitmap
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
    private val Y_CORDINATE_OFFSET = 100
    private val Y_CORDINATE_RANGE = 200
    private var mScreenWidth: Int = 0

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
        mPaint = Paint()

        mAnimPath = Path()
        mMatrix = Matrix()

        //Like emoticons
        val likeDrawable = resources.getDrawable(R.drawable.ic_like)
        val loveDrawable = resources.getDrawable(R.drawable.ic_love)
        val wowDrawable = resources.getDrawable(R.drawable.ic_wow)
        val sadDrawable = resources.getDrawable(R.drawable.ic_sad)
        val angryDrawable = resources.getDrawable(R.drawable.ic_angry)



        mLike48 = likeDrawable.drawableToBitmap()
//            BitmapFactory.decodeResource(context.resources, R.drawable.ic_like)
        //Love emoticons
        mLove48 = loveDrawable.drawableToBitmap()
//            BitmapFactory.decodeResource(context.resources, R.drawable.ic_love)
        //Wow emoticons
        mWow48 = wowDrawable.drawableToBitmap()
//            BitmapFactory.decodeResource(context.resources, R.drawable.ic_wow)
        //Sad emoticons
        mSad48 = sadDrawable.drawableToBitmap()
//            BitmapFactory.decodeResource(context.resources, R.drawable.ic_sad)
        //Angry emoticons
        mAngry48 = angryDrawable.drawableToBitmap()
//            BitmapFactory.decodeResource(context.resources, R.drawable.ic_angry)
    }

    override fun onDraw(canvas: Canvas) {
        mAnimPath?.let { mPaint?.let { it1 -> canvas.drawPath(it, it1) } }
        drawAllLiveEmoticons(canvas)
    }

    private fun drawAllLiveEmoticons(canvas: Canvas) {
        val iterator = mLiveEmoticons.listIterator()
        while (iterator.hasNext()) {
            val `object` = iterator.next()

            val xCoordinate = `object`.getxCordinate() - X_CORDINATE_STEP
            val yCoordinate = `object`.getyCordinate()
            `object`.setxCordinate(xCoordinate)
            if (xCoordinate > 0) {
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

        val xCoordinate = liveEmoticon.getxCordinate()
        var bitMap48: Bitmap? = null
        var scaled: Bitmap? = null

        val emoticons = liveEmoticon.getEmoticons() ?: return

        when (emoticons) {
            Emoticons.LIKE -> bitMap48 = mLike48
            Emoticons.LOVE -> bitMap48 = mLove48
            Emoticons.WOW -> bitMap48 = mWow48
            Emoticons.SAD -> bitMap48 = mSad48
            Emoticons.ANGRY -> bitMap48 = mAngry48
        }

        if (xCoordinate > mScreenWidth / 2) {
            if (bitMap48 != null && mMatrix != null) {
                canvas.drawBitmap(bitMap48, mMatrix!!, null)
            }
        } else if (xCoordinate > mScreenWidth / 4) {
            scaled = Bitmap.createScaledBitmap(
                bitMap48!!,
                3 * bitMap48.width / 4,
                3 * bitMap48.height / 4,
                false
            )
            canvas.drawBitmap(scaled, mMatrix!!, null)
        } else {
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
        val startXCoordinate = mScreenWidth
        val startYCoordinate = Random().nextInt(Y_CORDINATE_RANGE) + Y_CORDINATE_OFFSET
        val liveEmoticon = LiveEmoticon()
        liveEmoticon.LiveEmoticon(emoticons, startXCoordinate, startYCoordinate)
        mLiveEmoticons.add(liveEmoticon)
        invalidate()
    }

}