package zs.module.material.widget

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.EdgeEffect
import android.widget.RelativeLayout
import androidx.customview.widget.ViewDragHelper
import com.blankj.utilcode.util.AdaptScreenUtils
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.ScreenUtils
import zs.module.material.R

/**
 * @author zhangshuai@attrsense.com
 * @date 2022/12/14 11:24
 * @description
 */
class DragViewGroup @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RelativeLayout(context, attributeSet, defStyleAttr) {

    private lateinit var mDragView: View

    private val callback: ViewDragHelper.Callback = object : ViewDragHelper.Callback() {

        //View的拖拽状态改变时触发
        //STATE_IDLE:　未被拖拽
        //STATE_DRAGGING：正在被拖拽
        //STATE_SETTLING:　被安放到一个位置中的状态
        override fun onViewDragStateChanged(state: Int) {
            super.onViewDragStateChanged(state)
            when (state) {
                ViewDragHelper.STATE_IDLE -> {
                    Log.d("print_logs", "onViewDragStateChanged: STATE_IDLE 未被拖拽")
                }
                ViewDragHelper.STATE_DRAGGING -> {
                    Log.d("print_logs", "onViewDragStateChanged: STATE_DRAGGING 正在被拖拽")
                }
                ViewDragHelper.STATE_SETTLING -> {
                    Log.d("print_logs", "onViewDragStateChanged: STATE_SETTLING 被安放到一个位置中的状态")
                }
                else -> {}
            }

        }

        //拖拽时的（开始移动）触发
        //changeView:当前被拖拽的view
        //left:拖动时left坐标
        //top:拖动时top坐标
        //dx:拖拽时x轴偏移量
        //dy:拖拽时y轴偏移量
        override fun onViewPositionChanged(
            changedView: View,
            left: Int,
            top: Int,
            dx: Int,
            dy: Int
        ) {
            super.onViewPositionChanged(changedView, left, top, dx, dy)
        }

        //view被捕获时触发（也就是按下）
        //capturedChild：捕获的view
        //activePointerId：按下手指的id,多指触控时会用到
        //一般用于做准备初始化工作
        override fun onViewCaptured(capturedChild: View, activePointerId: Int) {
            super.onViewCaptured(capturedChild, activePointerId)
        }

        //view被放下时触发
        //releasedChild被放下的view
        //xvel：释放View的x轴方向上的加速度
        //yvel：释放View的y轴方向上的加速度
        //一般用于收尾工作
        override fun onViewReleased(releasedChild: View, xvel: Float, yvel: Float) {
            super.onViewReleased(releasedChild, xvel, yvel)
//            Log.w("print_logs", "DragViewGroup::onViewReleased: $xvel, $yvel")

            //让捕获到的View自动滚动到100，300位置，只能在这里使用这个方法
//            val px=ConvertUtils.dp2px(200F)
//            mDragHelper.settleCapturedViewAt(px, 0)


            //让捕获到的View在100，100，500，500这个范围内fling  ，只能在这里使用这个方法
//            val width = releasedChild.width
//            Log.i("print_logs", "onViewReleased: $px, $width")
//            mDragHelper.flingCapturedView(
//                0,
//                0,
//                ScreenUtils.getAppScreenWidth() - width,
//                ScreenUtils.getAppScreenHeight() - width - BarUtils.getNavBarHeight()
//            )


            //指定某个View自动滚动到500，500，初速度为0，可在任何地方调用
//            mDragHelper.smoothSlideViewTo(releasedChild,500, 0)

            ////这里判断吸附的边界，当子view的中间在屏幕左边，则吸附到左边，反之则吸附到右边
            if (releasedChild != null) {
                val x = releasedChild.right - (releasedChild.width / 2)
                val center = resources.displayMetrics.widthPixels / 2
                if (x < center) {
                    mDragHelper.settleCapturedViewAt(0, releasedChild.top)
                } else {
                    mDragHelper.settleCapturedViewAt(
                        resources.displayMetrics.widthPixels - releasedChild.width,
                        releasedChild.top
                    )
                }
            }


            //以上方法必须手动去刷新页面,想要其作用就要调用computeScroll()
            invalidate()
        }


        //是否开启边缘触摸，true代表开启，默认不开启
        //edgeFlags：触摸的位置EDGE_LEFT,EDGE_TOP,EDGE_RIGHT,EDGE_BOTTOM
        //使用较少，一般不重写
        override fun onEdgeLock(edgeFlags: Int): Boolean {
            return super.onEdgeLock(edgeFlags)
        }

        //边缘触摸时触发（需开启边缘触摸）
        //edgeFlags：触摸的位置EDGE_LEFT,EDGE_TOP,EDGE_RIGHT,EDGE_BOTTOM
        //pointerId： 按下手指的id,多指触控时会用到
        //使用较少，一般不重写
        override fun onEdgeTouched(edgeFlags: Int, pointerId: Int) {
            super.onEdgeTouched(edgeFlags, pointerId)
        }

        //边缘触摸时触发（需开启边缘触摸）
        //edgeFlags：触摸的位置EDGE_LEFT,EDGE_TOP,EDGE_RIGHT,EDGE_BOTTOM
        //pointerId： 按下手指的id,多指触控时会用到
        //使用较少，一般不重写
        override fun onEdgeDragStarted(edgeFlags: Int, pointerId: Int) {
            super.onEdgeDragStarted(edgeFlags, pointerId)
        }

        //寻找当前触摸点下的子View时会调用此方法，寻找到的View会提供给tryCaptureViewForDrag()来尝试捕获。
        //如果需要改变子View的遍历查询顺序可改写此方法，例如让下层的View优先于上层的View被选中。
        //使用较少，一般不重写
        override fun getOrderedChildIndex(index: Int): Int {
            return super.getOrderedChildIndex(index)
        }

        //暂不明确（返回任何值都可以移动，网上说的都是错的）
        //使用较少，一般不重写
        override fun getViewHorizontalDragRange(child: View): Int {
//            return super.getViewHorizontalDragRange(child)
            return if (mDragView == child) {
                measuredWidth - child.width
            } else {
                0
            }
        }

        //暂不明确（返回任何值都可以移动，网上说的都是错的）
        //使用较少，一般不重写
        override fun getViewVerticalDragRange(child: View): Int {
//            return super.getViewVerticalDragRange(child)
            return if (mDragView == child) {
                measuredHeight - child.height
            } else {
                0
            }
        }

        //决定child是否可以被拖拽
        //尝试捕获被拖拽的view，如果返回true代表可以被拖拽，返回false代表不可以被拖拽
        //var1:被拖拽的view
        //使用时判断需要被拖拽的view是否等等于var1。
        //一般判断很多view其中哪些是否可以移动时使用
        override fun tryCaptureView(child: View, pointerId: Int): Boolean = child == mDragView

        // 可决定child横向的偏移计算
        //返回view在水平方向的位置，
        //left:当前被拖拽的的view要移动到的的left值
        //dx:移动的偏移量
        //返回0则无法移动，通常直接返回left
        //一般必须重写此方法返回left
        override fun clampViewPositionHorizontal(child: View, left: Int, dx: Int): Int {
            // 两个if主要是为了让view在ViewGroup里拖动，不超过Viewgroup边界
            if (paddingLeft > left) {
                return paddingLeft
            }

            if (width - child.width < left) {
                return width - child.width
            }
            return left
        }

        // 可决定child竖向的偏移计算
        //返回view在竖直方向的位置，
        //top:当前被拖拽的的view要移动到的的left值
        //dy:移动的偏移量
        //返回0则无法移动，通常直接返回top
        //一般必须重写此方法返回top
        override fun clampViewPositionVertical(child: View, top: Int, dy: Int): Int {
            // 两个if主要是为了让view在ViewGroup里拖动，不超过Viewgroup边界
            if (paddingTop > top) {
                return paddingTop
            }

            if (height - child.height < top) {
                return height - child.height
            }
            return top
        }
    }

    private val mDragHelper = ViewDragHelper.create(this, 1.0f, callback)


//    override fun onInterceptHoverEvent(event: MotionEvent): Boolean {
//        return mDragHelper.shouldInterceptTouchEvent(event)
//    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_CANCEL,MotionEvent.ACTION_DOWN -> {
                mDragHelper.cancel()  //相当于调用 processTouchEvent收到ACTION_CANCEL
            }
            else -> {}
        }

        // 检查是否可以拦截touch事件
        // 如果onInterceptTouchEvent可以return true 则这里return true
        return mDragHelper.shouldInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        // 处理拦截到的事件
        // 这个方法会在返回前分发事件
        mDragHelper.processTouchEvent(event)
        return true
    }

    //ViewDragHelper内部是overScroller完成计算的,那么和overScroller一样需要重写computeScroll()一直刷新页面：
    override fun computeScroll() {
        super.computeScroll()
        //使用continueSettling(true)判断拖拽是否完成
        if (mDragHelper.continueSettling(true)) {
            invalidate()
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        //获取子View
        mDragView = getChildAt(0)
    }

}