package zs.android.module.constraintlayout

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import zs.android.module.constraintlayout.databinding.ActivityGridHelperBinding

class GridHelperActivity : AppCompatActivity() {

    private lateinit var mDataBinding: ActivityGridHelperBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDataBinding = ActivityGridHelperBinding.inflate(layoutInflater).apply {
            setContentView(this.root)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mDataBinding.unbind()
    }
}