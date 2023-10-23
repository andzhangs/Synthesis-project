package zs.android.module.constraintlayout

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import zs.android.module.constraintlayout.databinding.ActivityPlaceholderBinding

class PlaceholderActivity : AppCompatActivity() {

    private lateinit var mDataBinding: ActivityPlaceholderBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_placeholder)

        mDataBinding.acBtnAction1.setOnClickListener {
            mDataBinding.placeHolder1.setContentId(-1)
            mDataBinding.placeHolder2.setContentId(-1)
            mDataBinding.placeHolder1.setContentId(mDataBinding.acTvActual.id)
        }

        mDataBinding.acBtnAction2.setOnClickListener {
            mDataBinding.placeHolder1.setContentId(-1)
            mDataBinding.placeHolder2.setContentId(-1)
            mDataBinding.placeHolder2.setContentId(mDataBinding.acTvActual.id)
        }

        mDataBinding.acBtnActionClear.setOnClickListener {
            mDataBinding.placeHolder1.emptyVisibility = View.GONE
            mDataBinding.placeHolder2.emptyVisibility = View.GONE
            mDataBinding.placeHolder1.setContentId(-1)
            mDataBinding.placeHolder2.setContentId(-1)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mDataBinding.unbind()
    }
}