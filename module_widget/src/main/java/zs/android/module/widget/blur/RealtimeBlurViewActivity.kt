package zs.android.module.widget.blur

import android.annotation.SuppressLint
import android.util.Log
import android.view.MotionEvent
import android.view.View
import zs.android.base.lib.base.BaseBindingActivity
import zs.android.module.widget.BuildConfig
import zs.android.module.widget.R
import zs.android.module.widget.databinding.ActivityRealtimeBlurViewBinding
import kotlin.math.abs

class RealtimeBlurViewActivity : BaseBindingActivity<ActivityRealtimeBlurViewBinding>() {

    override fun setLayoutResId() = R.layout.activity_realtime_blur_view

    @SuppressLint("ClickableViewAccessibility")
    override fun init() {
        mDataBinding.acTvDragView.setOnTouchListener(object : View.OnTouchListener {
            var dx = 0f
            var dy = 0f
            var mScreenWidth = 0f
            var mScreenHeight = 0f
            var xDiff = 0f
            var yDiff = 0f
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                val targetView = mDataBinding.acTvDragView
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        mScreenWidth = v.resources.displayMetrics.widthPixels.toFloat()
                        mScreenHeight = v.resources.displayMetrics.heightPixels.toFloat()

                        //左上角x定点取值范围(0,xDiff)
                        xDiff = mScreenWidth - targetView.width.toFloat()
                        //左上角x定点取值范围(0,yDiff)
                        yDiff = mScreenHeight - targetView.height.toFloat()


                        dx = targetView.x - event.rawX
                        dy = targetView.y - event.rawY

                        Log.d(
                            "print_logs",
                            "ACTION_DOWN: ${targetView.x}, ${event.rawX}, $dx ,$mScreenWidth, $mScreenHeight ,$xDiff, $yDiff"
                        )
                    }

                    MotionEvent.ACTION_MOVE -> {
                        val xValue = event.rawX + dx
                        val yValue = event.rawY + dy

                        if (BuildConfig.DEBUG) {
                            Log.i("print_logs", "RealtimeBlurViewActivity::onTouch: ${targetView.x}, $xDiff")
                        }

                        targetView.x = if (xValue < 0) {
                            0f
                        } else if (targetView.x > xDiff) {
                            if (BuildConfig.DEBUG) {
                                Log.d("print_logs", "RealtimeBlurViewActivity::onTouch: 1 xDiff = $xDiff")
                            }
                            xDiff
                        } else {
                            if (BuildConfig.DEBUG) {
                                Log.w("print_logs", "RealtimeBlurViewActivity::onTouch: 2, xValue = $xValue")
                            }
                            xValue
                        }

                        targetView.y =
                            if (yValue < 0) {
                                0f
                            } else if (targetView.y > yDiff) {
                                yDiff
                            } else {
                                yValue
                            }


//                        Log.i("print_logs", "ACTION_MOVE: ${targetView.x}, ${event.rawX}, $dx")
                    }

                    MotionEvent.ACTION_UP -> {
                        targetView.x = abs(targetView.x)
                        targetView.y = abs(targetView.y)
                    }

                    else -> {}
                }

                return true
            }
        })
        mDataBinding.acTvDragView2.setOnTouchListener { v, event ->
            var lastX = 0f
            var lastY = 0f
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {

                }

                MotionEvent.ACTION_MOVE -> {

                }

                MotionEvent.ACTION_UP -> {

                }

                else -> {}
            }

            return@setOnTouchListener true
        }
    }
}