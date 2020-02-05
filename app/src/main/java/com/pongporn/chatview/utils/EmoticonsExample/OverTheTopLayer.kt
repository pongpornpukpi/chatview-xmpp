package com.pongporn.chatview.utils.EmoticonsExample

import android.graphics.Bitmap
import android.widget.FrameLayout
import android.view.ViewGroup
import android.app.Activity
import android.content.res.Resources
import java.lang.ref.WeakReference
import android.graphics.BitmapFactory
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.animation.Animation
import android.widget.ImageView

class OverTheTopLayer {

    class OverTheTopLayerException(msg: String) : RuntimeException(msg)

    private var mWeakActivity: WeakReference<Activity>? = null
    private var mWeakRootView: WeakReference<ViewGroup>? = null
    private var mCreatedOttLayer: FrameLayout? = null
    private var mScalingFactor = 1.0f
    private var mDrawLocation = intArrayOf(0, 0)
    private var mBitmap: Bitmap? = null

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

            val imageView = ImageView(activity)

            imageView.setImageBitmap(mBitmap)

            val minWidth = mBitmap?.width
            val minHeight = mBitmap?.height

            imageView.measure(
                View.MeasureSpec.makeMeasureSpec(minWidth!!, View.MeasureSpec.AT_MOST),
                View.MeasureSpec.makeMeasureSpec(minHeight!!, View.MeasureSpec.AT_MOST)
            )

            var params: FrameLayout.LayoutParams? = imageView.layoutParams as FrameLayout.LayoutParams?

            if (params == null) {
                params = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    Gravity.TOP
                )
                imageView.layoutParams = params
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

    fun applyAnimation(animation : Animation) {

        if(mCreatedOttLayer != null) {
            var drawnImageView : ImageView = mCreatedOttLayer?.getChildAt(0) as ImageView
//            [enter image description here][1]
            drawnImageView.startAnimation(animation)
        }
    }


}