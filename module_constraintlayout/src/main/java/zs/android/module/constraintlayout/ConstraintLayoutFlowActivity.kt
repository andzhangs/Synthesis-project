package zs.android.module.constraintlayout

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import zs.android.module.constraintlayout.databinding.ActivityConstraintLayoutFlowBinding

/**
 * 参考文档：https://juejin.cn/post/7270464435297206284
 */
class ConstraintLayoutFlowActivity : AppCompatActivity() {

    private lateinit var mDataBinding: ActivityConstraintLayoutFlowBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDataBinding = DataBindingUtil.setContentView(this,R.layout.activity_constraint_layout_flow)

    }
}