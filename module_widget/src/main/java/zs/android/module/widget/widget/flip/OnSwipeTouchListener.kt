package zs.android.module.widget.widget.flip

import android.content.Context
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import zs.android.module.widget.BuildConfig
import kotlin.math.abs

/**
 *
 * @author zhangshuai
 * @date 2023/11/27 16:36
 * @mark 自定义类描述
 */
class OnSwipeTouchListener(context: Context) : OnTouchListener {

    private lateinit var gestureDetector: GestureDetector
    private val swipeListeners: ArrayList<OnSwipeListener> = arrayListOf()

    companion object {
        private const val SWIPE_THRESHOLD = 100
        private const val SWIPE_VELOCITY_THRESHOLD = 100
    }

    init {
        gestureDetector = GestureDetector(context.applicationContext,
            object : GestureDetector.SimpleOnGestureListener() {
                override fun onFling(
                    e1: MotionEvent?,
                    e2: MotionEvent,
                    velocityX: Float,
                    velocityY: Float
                ): Boolean {
                    var result = false

                    try {
                        val diffY = e2.y - e1?.y!!
                        val diffX = e2.x - e1?.x!!

                        if (abs(diffX) > abs(diffY)) {
                            if (abs(diffX) > SWIPE_THRESHOLD && abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                                if (diffX > 0) {
                                    swipeListeners.forEach {
                                        it.toggleRight()
                                        if (BuildConfig.DEBUG) {
                                            Log.i("print_logs", "onFling: onSwipeRight")
                                        }
                                    }
                                } else {
                                    swipeListeners.forEach {
                                        it.toggleLeft()
                                        if (BuildConfig.DEBUG) {
                                            Log.i("print_logs", "onFling: onSwipeLeft")
                                        }
                                    }
                                }
                            }
                        } else if (abs(diffY) > SWIPE_THRESHOLD && abs(diffY) > SWIPE_VELOCITY_THRESHOLD) {
                            if (diffY > 0) {
                                swipeListeners.forEach {
                                    it.toggleDown()
                                    if (BuildConfig.DEBUG) {
                                        Log.i("print_logs", "onFling: onSwipeDown")
                                    }
                                }
                            } else {
                                swipeListeners.forEach {
                                    it.toggleUp()
                                    if (BuildConfig.DEBUG) {
                                        Log.i("print_logs", "onFling: onSwipeUp")
                                    }
                                }
                            }
                        }

                        result = true
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                    return result
                }
            })
    }


    fun addSwipeListener(listener: OnSwipeListener?) {
        if (listener != null && !swipeListeners.contains(listener)) {
            swipeListeners.add(listener)
        }
    }

    fun removeSwipeListener(listener: OnSwipeListener?) {
        if (listener != null && swipeListeners.contains(listener)) {
            swipeListeners.remove(listener)
        }
    }

    fun clear() {
        swipeListeners.clear()
    }

    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        return gestureDetector.onTouchEvent(event)
    }
}