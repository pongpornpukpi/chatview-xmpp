package com.pongporn.chatview.utils.EmoticonsExample

import android.graphics.Bitmap
import android.widget.FrameLayout
import android.view.ViewGroup
import android.app.Activity
import android.content.Context
import android.content.res.Resources
import java.lang.ref.WeakReference
import android.graphics.BitmapFactory
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.animation.Animation
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.view.marginTop
import androidx.core.view.setMargins
import com.pongporn.chatview.R
import com.pongporn.chatview.utils.CustomEmojiBang
import xyz.hanks.library.bang.SmallBangView

class OverTheTopLayer {

    class OverTheTopLayerException(msg: String) : RuntimeException(msg)

    private var mWeakActivity: WeakReference<Activity>? = null
    private var mWeakRootView: WeakReference<ViewGroup>? = null
    private var mCreatedOttLayer: FrameLayout? = null
    private var mScalingFactor = 1.0f
    private var mDrawLocation = intArrayOf(0, 0)
    private var mBitmap: Bitmap? = null
    private var mImageResId: Int? = 0
    private lateinit var imageView: CustomEmojiBang

    fun OverTheTopLayer() {}

    fun with(weakReferenceActivity: Activity): OverTheTopLayer {
        mWeakActivity = WeakReference(weakReferenceActivity)
        return this
    }

    fun generateBitmap(
        resources: Resources,
        drawableResId: Int,
        mScalingFactor: Float,
        location: IntArray?
    ): OverTheTopLayer {
        var location = location

        if (location == null) {
            location = intArrayOf(0, 0)
        } else if (location.size != 2) {
            throw OverTheTopLayerException("Requires location as an array of length 2 - [x,y]")
        }

        val bitmap = BitmapFactory.decodeResource(resources, drawableResId)

        val scaledBitmap = Bitmap.createScaledBitmap(
            bitmap,
            (bitmap.width * mScalingFactor).toInt(),
            (bitmap.height * mScalingFactor).toInt(),
            false
        )

        this.mBitmap = scaledBitmap

        this.mDrawLocation = location
        return this
    }

    fun setBitmap(bitmap: Bitmap, location: IntArray?): OverTheTopLayer {
        var location = location

        if (location == null) {
            location = intArrayOf(0, 0)
        } else if (location.size != 2) {
            throw OverTheTopLayerException("Requires location as an array of length 2 - [x,y]")
        }

        this.mBitmap = bitmap
        this.mDrawLocation = location
        return this
    }

    fun setType(mImageResId: Int): OverTheTopLayer {
        this.mImageResId = mImageResId
        return this
    }

    fun scale(scale: Float): OverTheTopLayer {

        if (scale <= 0) {
            throw OverTheTopLayerException("Scaling should be > 0")

        }
        this.mScalingFactor = scale


        return this
    }

    fun attachTo(rootView: ViewGroup): OverTheTopLayer {
        this.mWeakRootView = WeakReference(rootView)
        return this
    }

    fun create(): FrameLayout? {


        if (mCreatedOttLayer != null) {
            destroy()
        }

        if (mWeakActivity == null) {
            throw OverTheTopLayerException("Could not create the layer as not activity reference was provided.")
        }

        val activity = mWeakActivity?.get()

        if (activity != null) {
            var attachingView: ViewGroup? = null

            if (mWeakRootView != null && mWeakRootView?.get() != null) {
                attachingView = mWeakRootView?.get()
            } else {
                attachingView = activity.findViewById<ViewGroup>(android.R.id.content)
            }

            imageView = CustomEmojiBang(activity)

            when (mImageResId) {
                R.drawable.ic_like -> {
                    imageView.setBackGround(R.drawable.selected_emolike)
                    imageView.setColor(
                        ContextCompat.getColor(activity, R.color.colorBlue),
                        ContextCompat.getColor(activity, R.color.colorBlackBlue),
                        activity.resources.getIntArray(R.array.colorDotLike)
                    )
                }
                R.drawable.ic_love -> {
                    imageView.setBackGround(R.drawable.selected_emolove)
                    imageView.setColor(
                        ContextCompat.getColor(activity, R.color.colorRed),
                        ContextCompat.getColor(activity, R.color.colorBlackRed),
                        activity.resources.getIntArray(R.array.colorDotLove)
                    )
                }
                R.drawable.ic_sad -> {
                    imageView.setBackGround(R.drawable.selected_emosad)
                    imageView.setColor(
                        ContextCompat.getColor(activity, R.color.colorYellow),
                        ContextCompat.getColor(activity, R.color.colorBlackYellow),
                        activity.resources.getIntArray(R.array.colorDotEmoticon)
                    )
                }
                R.drawable.ic_wow -> {
                    imageView.setBackGround(R.drawable.selected_emowow)
                    imageView.setColor(
                        ContextCompat.getColor(activity, R.color.colorYellow),
                        ContextCompat.getColor(activity, R.color.colorBlackYellow),
                        activity.resources.getIntArray(R.array.colorDotEmoticon)
                    )
                }
                R.drawable.ic_angry -> {
                    imageView.setBackGround(R.drawable.selected_emoangry)
                    imageView.setColor(
                        ContextCompat.getColor(activity, R.color.colorYellow),
                        ContextCompat.getColor(activity, R.color.colorRed),
                        activity.resources.getIntArray(R.array.colorDotAngry)
                    )
                }
            }

            val minWidth = mBitmap?.width!!
            val minHeight = mBitmap?.height!!

            imageView.measure(
                View.MeasureSpec.makeMeasureSpec(minWidth, View.MeasureSpec.AT_MOST),
                View.MeasureSpec.makeMeasureSpec(minHeight, View.MeasureSpec.AT_MOST)
            )

            var params: FrameLayout.LayoutParams? =
                imageView.layoutParams as FrameLayout.LayoutParams?

            var smallBang = imageView.findViewById<FrameLayout>(R.id.small_bang)
            smallBang.layoutParams = LinearLayout.LayoutParams(
                mBitmap?.width!!,
                mBitmap?.height!!
            )

            if (params == null) {
                params = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    Gravity.TOP
                )
            }

            val xPosition = mDrawLocation[0]
            val yPosition = mDrawLocation[1]

            params.width = minWidth
            params.height = minHeight

            params.leftMargin = xPosition
            params.topMargin = yPosition

            imageView.layoutParams = params

            val ottLayer = FrameLayout(activity)
            val topLayerParam = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT,
                Gravity.TOP
            )
            ottLayer.layoutParams = topLayerParam

            ottLayer.addView(imageView)

            attachingView!!.addView(ottLayer)

            mCreatedOttLayer = ottLayer


        } else {
            Log.e(
                OverTheTopLayer::class.java.simpleName,
                "Could not create the layer. Reference to the activity was lost"
            )
        }

        return mCreatedOttLayer

    }

    fun destroy() {


        if (mWeakActivity == null) {
            throw OverTheTopLayerException("Could not create the layer as not activity reference was provided.")
        }

        val activity = mWeakActivity?.get()

        if (activity != null) {
            var attachingView: ViewGroup? = null


            if (mWeakRootView != null && mWeakRootView?.get() != null) {
                attachingView = mWeakRootView?.get()
            } else {
                attachingView = activity.findViewById<View>(android.R.id.content) as ViewGroup
            }

            if (mCreatedOttLayer != null) {

                attachingView!!.removeView(mCreatedOttLayer)
                mCreatedOttLayer = null
            }


        } else {

            Log.e(
                OverTheTopLayer::class.java.simpleName,
                "Could not destroy the layer as the layer was never created."
            )

        }

    }

    fun applyAnimation(animation: Animation) {

        if (mCreatedOttLayer != null) {
            val drawnImageView = mCreatedOttLayer?.getChildAt(0) as FrameLayout
            val linearLayoutView = drawnImageView.getChildAt(0) as LinearLayout
            val smallBangView = linearLayoutView.getChildAt(0) as SmallBangView
            android.os.Handler().postDelayed({
                smallBangView.isSelected = true
                smallBangView.likeAnimation()
            }, animation.duration / 2 + 300)
            drawnImageView.startAnimation(animation)
        }
    }


}