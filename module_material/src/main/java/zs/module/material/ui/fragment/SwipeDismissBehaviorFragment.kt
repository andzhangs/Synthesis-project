package zs.module.material.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.behavior.SwipeDismissBehavior
import zs.module.material.BuildConfig
import zs.module.material.R
import zs.module.material.base.BaseMaterialFragment

/**
 * 实现侧滑删除的效果
 */
class SwipeDismissBehaviorFragment private constructor(mIndexName: String) :
    BaseMaterialFragment(mIndexName) {

    companion object {

        @JvmStatic
        fun newInstance() = SwipeDismissBehaviorFragment("SwipeDismissBehavior")
    }

    override fun setLayoutRes() = R.layout.fragment_swipe_dismiss_behavior

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val coordinatorLayout=mRootView.findViewById<CoordinatorLayout>(R.id.coordinatorLayout)
        val swipeView = mRootView.findViewById<AppCompatTextView>(R.id.tv_swip)
        val blowView = mRootView.findViewById<AppCompatTextView>(R.id.tv_below)

        val swipeBehavior = SwipeDismissBehavior<View>()
        //指定用户可以从哪个方向滑动视图来关闭或取消视图。
        swipeBehavior.setSwipeDirection(SwipeDismissBehavior.SWIPE_DIRECTION_START_TO_END)
        //指定用户需要滑动多远才能关闭或取消视图。
        swipeBehavior.setDragDismissDistance(200f)
        //允许消失的子View
        swipeBehavior.canSwipeDismissView(swipeView)
        //设置灵敏度
        swipeBehavior.setSensitivity(0.5f)
        //设置开始Alpha滑动距离
        swipeBehavior.setStartAlphaSwipeDistance(100f)
        //设置结束Alpha滑动距离
        swipeBehavior.setEndAlphaSwipeDistance(100f)

//        swipeBehavior.blocksInteractionBelow(coordinatorLayout,blowView)

        swipeBehavior.listener = object : SwipeDismissBehavior.OnDismissListener {
            override fun onDismiss(view: View?) {
                if (BuildConfig.DEBUG) {
                    Log.i("print_logs", "SwipeDismissBehaviorFragment::onDismiss: ")
                }
            }

            override fun onDragStateChanged(state: Int) {
                if (BuildConfig.DEBUG) {
                    Log.i("print_logs", "SwipeDismissBehaviorFragment::onDragStateChanged: ")
                }
            }
        }
        val layoutParams = swipeView.layoutParams as CoordinatorLayout.LayoutParams
        layoutParams.behavior = swipeBehavior
    }

}