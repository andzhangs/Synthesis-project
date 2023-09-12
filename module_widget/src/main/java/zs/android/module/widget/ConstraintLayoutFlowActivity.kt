package zs.android.module.widget

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import zs.android.module.widget.databinding.ActivityConstraintLayoutFlowBinding

/**
 * 参考文档：https://juejin.cn/post/7270464435297206284
 */
class ConstraintLayoutFlowActivity : AppCompatActivity() {

    private lateinit var mDataBinding: ActivityConstraintLayoutFlowBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDataBinding = ActivityConstraintLayoutFlowBinding.inflate(layoutInflater)
        setContentView(mDataBinding.root)
    }
}