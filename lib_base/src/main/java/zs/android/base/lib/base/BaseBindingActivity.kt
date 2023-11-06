package zs.android.base.lib.base

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

/**
 *
 * @author zhangshuai
 * @date 2023/10/27 14:38
 * @mark 自定义类描述
 */
abstract class BaseBindingActivity<DB : ViewDataBinding> : AppCompatActivity() {

    lateinit var mDataBinding: DB

    @LayoutRes
    protected abstract fun setLayoutResId(): Int

    protected abstract fun init()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDataBinding = DataBindingUtil.setContentView(this, setLayoutResId())
        mDataBinding.lifecycleOwner = this
        init()
    }

    override fun onDestroy() {
        super.onDestroy()
        mDataBinding.unbind()
    }
}