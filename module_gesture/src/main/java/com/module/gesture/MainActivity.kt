package com.module.gesture

import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.module.gesture.databinding.ActivityMainBinding

/**
 * 主要的触摸事件：
 *  ACTION_DOWN：手指碰到屏幕的瞬间；
 *  ACTION_MOVE：手指在屏幕上滑动；
 *  ACTION_UP：手指离开屏幕；
 *  ACTION_CANCEL：当系统想要取消当前的触摸事件时触发；
 *
 *  单击事件
 *      1. 实现流程：
 *         当用户触摸屏幕时，触发ACTION_DOWN事件。如果用户在短时间内抬起手指，
 *         触发ACTION_UP事件，且手指没有移动或仅移动距离小于阈值，则系统认为用户执行的单击操作。
 *      2. 实现：View类通过setOnClickListener()提供了单击事件监听器。系统会自动监控触摸事件的序列，
 *      满足单击条件时调用onClick(View v)方法。
 *
 *  手势事件
 *      1. 实现流程：
 *              双击：当检测到两个ACTION_DOWN事件之间的时间间隔小于双击阈值即为双击。
 *              长按：当检测到ACTION_DOWN事件，并且长时间阈值内没有出发ACTION_UP事件同时ACTION_MOVE的移动距离小于一定的阈值，识别为长按。
 *              滑动：当检测到ACTION_DOWN事件后有连续的ACTION_MOVE事件且移动距离达到滑动阈值即为滑动事件。
 *      2. 实现：GestureDetector类提供了单击事件onSingleTapUp，双击事件onDoubleTap，长按事件onLongPress，滑动事件onFling。
 *
 *  多点触控事件
 *      1、多点触摸事件说明：
 *              当检测到第二根手指触摸屏幕时，触发ACTION_POINTER_DOWN事件表示多点触控开始。
 *              当多个触摸点发生变化，触发ACTION_MOVE事件并且可以通过getX(pointerIndex)和getY(pointerIndex)获取每个触摸点的坐标。
 *              当某个手指抬起时，触摸ACTION_POINTER_UP事件，表示少了一个触摸点。
*       2. 实现流程：
 *              缩放：通过计算两根手指的距离变化，实现放大或缩小的手势。
 *              旋转：通过计算两根手指的相对角度变化，实现旋转操作。
 *      3. 实现类：ScaleGestureDetector类专门用于检测缩放手势，提供了onScale、onScaleBegin、onScaleEnd方法监听。
 *
 *   只要明白了各种手势事件的实现流程，对于自定义View的开发能有很大的帮助的。
 *   由此可以得出结论，手势交互都是基于触摸事件去实现的，也能验证标题的正确性。
 *   当然还有很多手势事件，触摸事件只是其触发的一个条件，如拖拽事件。
 */
class MainActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event != null) {
            mScaleGestureDetector.onTouchEvent(event)
            mGestureDetector.onTouchEvent(event)
        }
        return super.onTouchEvent(event)
    }

    /**
     * 手势缩放
     */
    private val mScaleGestureDetector: ScaleGestureDetector by lazy {
        ScaleGestureDetector(this, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                Log.i("print_logs", "MainActivity::onScale: 处理视图缩放，如改变视图大小 ${detector.previousSpanX}, ${detector.currentSpanX}")
                return super.onScale(detector)
            }

            override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
                Log.i("print_logs", "MainActivity::onScaleBegin: 缩放开始时调用，返回是否开始处理缩放手势")
                return super.onScaleBegin(detector)
            }

            override fun onScaleEnd(detector: ScaleGestureDetector) {
                super.onScaleEnd(detector)
                Log.i("print_logs", "MainActivity::onScaleEnd: 缩放结束时调用")
            }
        })

    }

    private val mGestureDetector :GestureDetector by lazy {
        GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onDown(e: MotionEvent): Boolean {
                if (BuildConfig.DEBUG) {
                    Log.d("print_logs", "MainActivity::onDown: 手指按下屏幕时调用")
                }

                return super.onDown(e)
            }

            override fun onShowPress(e: MotionEvent) {
                super.onShowPress(e)
                if (BuildConfig.DEBUG) {
                    Log.d("print_logs", "MainActivity::onShowPress: 手指按下后，还没有移动或抬起时调用，用于显示按下的视觉反馈")
                }

            }

            override fun onSingleTapUp(e: MotionEvent): Boolean {
                if (BuildConfig.DEBUG) {
                    Log.e("print_logs", "MainActivity::onSingleTapUp: 手指轻击屏幕后抬起时调用")
                }
                return super.onSingleTapUp(e)
            }

            override fun onScroll(
                e1: MotionEvent,
                e2: MotionEvent,
                distanceX: Float,
                distanceY: Float
            ): Boolean {
                if (BuildConfig.DEBUG) {
                    Log.e("print_logs", "MainActivity::onScroll: 手指在屏幕上滑动时调用，e1是起始事件，e2是当前事件，distanceX和distanceY是滑动的距离 $distanceX, $distanceY")
                }
                return super.onScroll(e1, e2, distanceX, distanceY)
            }

            override fun onLongPress(e: MotionEvent) {
                super.onLongPress(e)
                if (BuildConfig.DEBUG) {
                    Log.d("print_logs", "MainActivity::onLongPress: 手指长按屏幕时调用")
                }

            }

            override fun onFling(
                e1: MotionEvent,
                e2: MotionEvent,
                velocityX: Float,
                velocityY: Float
            ): Boolean {
                if (BuildConfig.DEBUG) {
                    Log.e("print_logs", "MainActivity::onFling: 手指快速滑动（抛）屏幕时调用，e1是起始事件，e2是结束事件，velocityX和velocityY是滑动的速度 $velocityX, $velocityY")
                }
                return super.onFling(e1, e2, velocityX, velocityY)
            }

            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                if (BuildConfig.DEBUG) {
                    Log.d("print_logs", "MainActivity::onSingleTapConfirmed: 单次点击确认")
                }

                return super.onSingleTapConfirmed(e)
            }

            override fun onDoubleTap(e: MotionEvent): Boolean {
                if (BuildConfig.DEBUG) {
                    Log.e("print_logs", "MainActivity::onDoubleTap: 双击屏幕时调用")
                }
                return super.onDoubleTap(e)
            }

            override fun onDoubleTapEvent(e: MotionEvent): Boolean {
                if (BuildConfig.DEBUG) {
                    Log.d("print_logs", "MainActivity::onDoubleTapEvent: ")
                }

                return super.onDoubleTapEvent(e)
            }

            override fun onContextClick(e: MotionEvent): Boolean {
                if (BuildConfig.DEBUG) {
                    Log.d("print_logs", "MainActivity::onContextClick: ")
                }

                return super.onContextClick(e)
            }
        })
    }
}