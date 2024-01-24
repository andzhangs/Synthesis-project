package zs.android.module.widget

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetBehavior
import zs.android.module.widget.databinding.ActivityCoordinatorLayoutNestedScrollViewBinding

class CoordinatorLayoutNestedScrollViewActivity : AppCompatActivity() {

    /**
     * 滑动控件中需要添加layout_behavior属性，这里默认使用BottomSheetBehavior。
     * behavior_hideable：代表是否隐藏
     * behavior_peekHeight：代表滑动控件初始展示的高度
     *
     * isFitToContents：是否填充整个内容
     * expandedOffset：展开后距离顶部的高度
     * halfExpandedRatio：半展开占比
     * setState：设置当前状态：隐藏、半展开、全展开等等
     * setPeekHeight：设置初始显示高度xx
     */

    private lateinit var mDataBinding: ActivityCoordinatorLayoutNestedScrollViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDataBinding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_coordinator_layout_nested_scroll_view
        )

        BottomSheetBehavior.from(mDataBinding.nestedScrollView).let {
            //展开后开度填充Parent的高度
            it.isFitToContents = false
            //setFitToContents 为false时，展开后距离顶部的位置（Parent会以PaddingTop填充）
            //顶部的距离
            it.expandedOffset = 0
            //半展开占比
            it.halfExpandedRatio = 0.5f
            it.isHideable = false
            it.setPeekHeight(150, true) //有动画
            it.state = BottomSheetBehavior.STATE_HIDDEN

            it.addBottomSheetCallback(object :BottomSheetBehavior.BottomSheetCallback(){
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    if (BuildConfig.DEBUG) {
                        Log.i(
                            "print_logs",
                            "CoordinatorLayoutNestedScrollViewActivity::onStateChanged: $newState"
                        )
                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    if (BuildConfig.DEBUG) {
                        Log.i("print_logs", "CoordinatorLayoutNestedScrollViewActivity::onSlide: $slideOffset")
                    }
                }

            })
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mDataBinding.unbind()
    }
}