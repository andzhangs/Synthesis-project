package zs.module.material.widget

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.customview.widget.ViewDragHelper
import com.blankj.utilcode.BuildConfig
import zs.module.material.R

/**
 * https://www.cnblogs.com/guanxinjing/p/17463237.html
 */
class DragLayoutView(context: Context, attrs: AttributeSet?) : ConstraintLayout(context, attrs) {


    private lateinit var mViewDragHelper: ViewDragHelper

    /**
     * 随意拖动
     */
    private lateinit var mAcIvAny: AppCompatImageView

    /**
     * 顶部回弹效果
     */
    private lateinit var mAcIvResilienceTop: AppCompatImageView

    //记录当前控件初始化的位置
    private var mDefaultTopX = 0
    private var mDefaultTopY = 0

    /**
     * 底部回弹效果
     */
    private lateinit var mAcIvResilienceBottom: AppCompatImageView

    //记录当前控件初始化的位置
    private var mDefaultBottomX = 0
    private var mDefaultBottomY = 0

    /**
     * 左边缘拖拽监听
     */
    private lateinit var mAcIvSideLeft: AppCompatImageView
    private var mCurrentLeftX = 0
    private var mCurrentLeftY = 0

    /**
     * 右边缘拖拽监听
     */
    private lateinit var mAcIvSideRight: AppCompatImageView
    private var mCurrentRightX = 0
    private var mCurrentRightY = 0

    init {
        /**
         * 第一个参数是实现拖动的ViewGroup父类布局，第二个参数是拖动时的灵敏度，拖动开始的敏感程度的乘数。值越大越敏感。1.0f是正常的。
         */
        mViewDragHelper = ViewDragHelper.create(this, 1.0f, object : ViewDragHelper.Callback() {
            /**
             * 是否允许view的拖动功能，返回true是允许拖动，返回false是不允许拖动
             */
            override fun tryCaptureView(child: View, pointerId: Int): Boolean {
                if (BuildConfig.DEBUG) {
                    Log.i("print_logs", "DragLayoutView::tryCaptureView: ")
                }
                return true
            }

            override fun onViewCaptured(capturedChild: View, activePointerId: Int) {
                super.onViewCaptured(capturedChild, activePointerId)
                if (BuildConfig.DEBUG) {
                    Log.i(
                        "print_logs",
                        "DragLayoutView::onViewCaptured: activePointerId= $activePointerId"
                    )
                }
            }

            /**
             * 控制横向方向的拖动位移，如果不重写此方法默认是不允许横向运动的，按照下面重写方法后可以允许横向方向的拖动
             */
            override fun clampViewPositionHorizontal(child: View, left: Int, dx: Int): Int {
                if (BuildConfig.DEBUG) {
//                    Log.i("print_logs", "DragLayoutView::clampViewPositionHorizontal: ")
                }
                val leftBound = paddingLeft
                val rightBound = width - child.width - paddingRight
                return left.coerceAtLeast(leftBound).coerceAtMost(rightBound)
            }

            /**
             * 控制垂直方向的拖动位移，如果不重写此方法默认是不允许垂直运动的，按照下面重写方法后可以允许垂直方向的拖动
             */
            override fun clampViewPositionVertical(child: View, top: Int, dy: Int): Int {
                if (BuildConfig.DEBUG) {
//                    Log.i("print_logs", "DragLayoutView::clampViewPositionVertical: ")
                }
                val topBound = paddingTop
                val bottomBound = height - child.height - paddingBottom
                return top.coerceAtLeast(topBound).coerceAtMost(bottomBound)
            }

            override fun onViewDragStateChanged(state: Int) {
                super.onViewDragStateChanged(state)
                when (state) {
                    ViewDragHelper.STATE_IDLE -> {
                        if (BuildConfig.DEBUG) {
                            Log.i(
                                "print_logs",
                                "DragLayoutView::onViewDragStateChanged: STATE_IDLE 空闲"
                            )
                        }
                    }

                    ViewDragHelper.STATE_SETTLING -> {
                        if (BuildConfig.DEBUG) {
                            Log.i(
                                "print_logs",
                                "DragLayoutView::onViewDragStateChanged: STATE_SETTLING"
                            )
                        }
                    }

                    ViewDragHelper.STATE_DRAGGING -> {
                        if (BuildConfig.DEBUG) {
                            Log.i(
                                "print_logs",
                                "DragLayoutView::onViewDragStateChanged: STATE_DRAGGING 拖动中..."
                            )
                        }
                    }

                    else -> {}
                }
            }

            override fun onViewPositionChanged(
                changedView: View,
                left: Int,
                top: Int,
                dx: Int,
                dy: Int
            ) {
                super.onViewPositionChanged(changedView, left, top, dx, dy)
                if (BuildConfig.DEBUG) {
                    Log.i(
                        "print_logs",
                        "DragLayoutView::onViewPositionChanged: left= $left, top= $top, dx= $dx, dy= $dy"
                    )
                }

                if (changedView == mAcIvSideLeft) {
                    mCurrentLeftX = left
                    mCurrentLeftY = top
                }

                if (changedView == mAcIvSideRight) {
                    mCurrentRightX = left
                    mCurrentRightY = top
                }
            }

            /**
             * 当子视图不再被主动拖动时调用。
             * releasedChild – 捕获的子视图现在被释放
             * @param xvel – 指针离开屏幕时的X速度，单位为像素每秒。
             * @param yvel – 指针离开屏幕时的Y速度，单位为像素/秒。
             */
            override fun onViewReleased(releasedChild: View, xvel: Float, yvel: Float) {
                if (BuildConfig.DEBUG) {
                    Log.i("print_logs", "DragLayoutView::onViewReleased: 释放控件")
                }
                when (releasedChild) {
                    mAcIvResilienceTop -> {       //顶部任意拖动回弹
                        mViewDragHelper.settleCapturedViewAt(mDefaultTopX, mDefaultTopY)
                        invalidate()
                    }

                    mAcIvResilienceBottom -> {   //底部任意拖动回弹
                        mViewDragHelper.settleCapturedViewAt(mDefaultBottomX, mDefaultBottomY)
                        invalidate()
                    }

                    mAcIvSideLeft -> {
                        //半屏宽度
                        val halfScreenWidth = width / 2
                        //子控件半个宽度
                        val halfChildWidth = releasedChild.width / 2
                        mCurrentLeftX += halfChildWidth

                        if (mCurrentLeftX <= halfScreenWidth) { //靠左边
                            mViewDragHelper.settleCapturedViewAt(0, mCurrentLeftY)
                        } else {  //靠右边
                            mViewDragHelper.settleCapturedViewAt(
                                width - releasedChild.width - paddingRight,
                                mCurrentLeftY
                            )
                        }
                        invalidate()
                    }

                    mAcIvSideRight -> {
                        //半屏高度
                        val halfScreenHeight = height / 2
                        //子控件半个宽度
                        val halfChildHeight = releasedChild.height / 2
                        mCurrentRightY += halfChildHeight

                        if (mCurrentRightY <= halfScreenHeight) { //靠顶部
                            mViewDragHelper.settleCapturedViewAt(mCurrentRightX, 0)
                        } else {  //靠底部
                            mViewDragHelper.settleCapturedViewAt(
                                mCurrentRightX,
                                height - releasedChild.height - paddingBottom
                            )
                        }
                        invalidate()
                    }

                    else -> {
                        super.onViewReleased(releasedChild, xvel, yvel)
                    }
                }
            }

            override fun onEdgeLock(edgeFlags: Int): Boolean {
                if (BuildConfig.DEBUG) {
                    Log.i(
                        "print_logs",
                        "DragLayoutView::onEdgeLock: ${super.onEdgeLock(edgeFlags)}"
                    )
                }
                return super.onEdgeLock(edgeFlags)
            }

            override fun getOrderedChildIndex(index: Int): Int {
                if (BuildConfig.DEBUG) {
                    Log.i(
                        "print_logs",
                        "DragLayoutView::getOrderedChildIndex: ${super.getOrderedChildIndex(index)}"
                    )
                }
                return super.getOrderedChildIndex(index)
            }

            override fun getViewHorizontalDragRange(child: View): Int {
                if (BuildConfig.DEBUG) {
                    Log.i(
                        "print_logs",
                        "DragLayoutView::getViewHorizontalDragRange: ${
                            super.getViewHorizontalDragRange(child)
                        }"
                    )
                }
                return super.getViewHorizontalDragRange(child)
            }

            override fun getViewVerticalDragRange(child: View): Int {
                if (BuildConfig.DEBUG) {
                    Log.i(
                        "print_logs",
                        "DragLayoutView::getViewVerticalDragRange: ${
                            super.getViewVerticalDragRange(child)
                        }"
                    )
                }
                return super.getViewVerticalDragRange(child)
            }

            override fun onEdgeTouched(edgeFlags: Int, pointerId: Int) {
                super.onEdgeTouched(edgeFlags, pointerId)
                if (BuildConfig.DEBUG) {
                    Log.i("print_logs", "DragLayoutView::onEdgeTouched: $edgeFlags, $pointerId")
                }
            }

            override fun onEdgeDragStarted(edgeFlags: Int, pointerId: Int) {
                super.onEdgeDragStarted(edgeFlags, pointerId)
                when (edgeFlags) {
                    ViewDragHelper.EDGE_LEFT -> {
                        if (BuildConfig.DEBUG) {
                            Log.d(
                                "print_logs",
                                "DragLayoutView::onEdgeDragStarted: 已经移动到最左边了 指针id${pointerId}"
                            )
                        }
                    }

                    ViewDragHelper.EDGE_RIGHT -> {
                        if (BuildConfig.DEBUG) {
                            Log.d(
                                "print_logs",
                                "DragLayoutView::onEdgeDragStarted: 已经移动到最右边了 指针id${pointerId}"
                            )
                        }
                    }

                    ViewDragHelper.EDGE_TOP -> {
                        if (BuildConfig.DEBUG) {
                            Log.d(
                                "print_logs",
                                "DragLayoutView::onEdgeDragStarted: 已经移动到最顶部了 指针id${pointerId}"
                            )
                        }
                    }

                    ViewDragHelper.EDGE_BOTTOM -> {
                        if (BuildConfig.DEBUG) {
                            Log.d(
                                "print_logs",
                                "DragLayoutView::onEdgeDragStarted: 已经移动到最底部了 指针id${pointerId}"
                            )
                        }
                    }

                    else -> {}
                }
                mViewDragHelper.captureChildView(mAcIvAny, pointerId)
            }
        })
        //设置需要监听的四个方向的编译，只能设置一个方向，并且在上面的onEdgeDragStarted里回调触发
        mViewDragHelper.setEdgeTrackingEnabled(ViewDragHelper.EDGE_ALL)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        mAcIvAny = this.findViewById(R.id.acIv_any)
        mAcIvResilienceTop = this.findViewById(R.id.acIv_resilience_top)
        mAcIvResilienceBottom = this.findViewById(R.id.acIv_resilience_bottom)
        mAcIvSideLeft = this.findViewById(R.id.acIv_side_left)
        mAcIvSideRight = this.findViewById(R.id.acIv_side_right)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        mDefaultTopX = mAcIvResilienceTop.left
        mDefaultTopY = mAcIvResilienceTop.top

        mDefaultBottomX = mAcIvResilienceBottom.left
        mDefaultBottomY = mAcIvResilienceBottom.top

        mCurrentLeftX = mAcIvSideLeft.left
        mCurrentLeftY = mAcIvSideLeft.top

        mCurrentRightX = mAcIvSideRight.left
        mCurrentRightY = mAcIvSideRight.top
    }


    /**
     * 注意这个重写的computeScroll与设置的continueSettling是关键，如果不重写此方法，settleCapturedViewAt方法就没有效果
     */
    override fun computeScroll() {
        super.computeScroll()
        if (mViewDragHelper.continueSettling(true)) {
            invalidate()
        }
    }

    /**
     * 注意，你需要重写onInterceptTouchEvent方法并且将触摸拦截交予ViewDragHelper的shouldInterceptTouchEvent，使其可以重新分配触控事件
     */
    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return mViewDragHelper.shouldInterceptTouchEvent(ev)
    }

    /**
     * 注意，你需要重写onTouchEvent，并且将mViewDragHelper的processTouchEvent实现，使其可以实现拖动子view的效果
     */
    override fun onTouchEvent(event: MotionEvent): Boolean {
        mViewDragHelper.processTouchEvent(event)
        return true
    }
}