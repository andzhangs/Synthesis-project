package zs.android.module.constraintlayout

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import zs.android.module.constraintlayout.databinding.ActivityConstraintLayoutFlowBinding

/**
 * 参考文档：https://juejin.cn/post/7270464435297206284
 * https://mp.weixin.qq.com/s?__biz=MzAxNzMxNzk5OQ==&mid=2649487224&idx=1&sn=c78c93cab76af8084fb901b32c24a4ad&chksm=83f83e78b48fb76e1a76b908247d0a3988c12f4dbed4be6f307dee637b45cccfa36648419600&scene=178&cur_album_id=1567168071180566530#rd
 */
class ConstraintLayoutFlowActivity : AppCompatActivity() {

    private lateinit var mDataBinding: ActivityConstraintLayoutFlowBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDataBinding = DataBindingUtil.setContentView(this,R.layout.activity_constraint_layout_flow)

    }
}