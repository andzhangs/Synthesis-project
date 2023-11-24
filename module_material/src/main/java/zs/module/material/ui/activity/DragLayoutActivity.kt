package zs.module.material.ui.activity

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import zs.module.material.R
import zs.module.material.databinding.ActivityDragLayoutBinding

class DragLayoutActivity : AppCompatActivity() {

    private lateinit var mDataBinding: ActivityDragLayoutBinding

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_drag_layout)
        mDataBinding.lifecycleOwner = this
    }

    override fun onDestroy() {
        super.onDestroy()
        mDataBinding.unbind()
    }
}