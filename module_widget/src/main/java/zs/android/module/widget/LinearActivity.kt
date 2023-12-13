package zs.android.module.widget

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import zs.android.module.widget.databinding.ActivityLinearBinding

class LinearActivity : AppCompatActivity() {

    private lateinit var mDataBinding: ActivityLinearBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_linear)

        var isVisibility = false

        mDataBinding.acBtnToggle.setOnClickListener {
            isVisibility = !isVisibility
            mDataBinding.acTv1.visibility = if (isVisibility) View.VISIBLE else View.GONE
            mDataBinding.acTv2.visibility = if (isVisibility) View.VISIBLE else View.GONE
            mDataBinding.acTv3.visibility = if (isVisibility) View.VISIBLE else View.GONE
            mDataBinding.acTv4.visibility = if (isVisibility) View.VISIBLE else View.GONE
            mDataBinding.acTv5.visibility = if (isVisibility) View.VISIBLE else View.GONE
            mDataBinding.acTv6.visibility = if (isVisibility) View.VISIBLE else View.GONE
            mDataBinding.acTv7.visibility = if (isVisibility) View.VISIBLE else View.GONE
            mDataBinding.acTv8.visibility = if (isVisibility) View.VISIBLE else View.GONE
            mDataBinding.acTv9.visibility = if (isVisibility) View.VISIBLE else View.GONE
            mDataBinding.acBtnToggle.text = if (isVisibility) "收起" else "展开"
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mDataBinding.unbind()
    }
}