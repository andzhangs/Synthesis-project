package zs.android.module.widget

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Point
import android.os.Bundle
import android.view.Gravity
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import com.google.android.material.snackbar.Snackbar
import zs.android.module.widget.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var mDataBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDataBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mDataBinding.root)
        clickListener()
    }

    private val mToast: Toast by lazy {
        val view = AppCompatTextView(this).apply {
            text = "哈哈哈"
            setPadding(15, 10, 15, 10)
            setBackgroundColor(Color.BLACK)
            setTextColor(Color.WHITE)
            textSize = 18f
            gravity = Gravity.CENTER

            val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val point = Point().apply {

            }
            windowManager.defaultDisplay.getSize(point)

            minWidth = point.x
            minHeight = 48 * 2
        }

        Toast(this).apply {
            duration = Toast.LENGTH_SHORT
            setView(view)
            setGravity(Gravity.BOTTOM, 0, -50)
        }
    }

    private fun clickListener() {
        mDataBinding.acBtnSnackBar.setOnClickListener {
            Snackbar.make(it, "你哈", 1000)
                .setAction("取消") {
                    Toast.makeText(this@MainActivity, "触发 点击", Toast.LENGTH_SHORT).show()
                }
                .setActionTextColor(Color.GREEN)
                .setTextColor(Color.BLACK)
                .setBackgroundTint(Color.WHITE)
                .setAnchorView(mDataBinding.acBtnRebound)
//                .setDuration(Snackbar.LENGTH_SHORT)
                .show()
        }

        mDataBinding.acBtnToast.setOnClickListener {
            mToast.view?.apply {
                if (!isShown) {
                    mToast.show()
                } else {
                    mToast.cancel()
                }
            }
        }

        mDataBinding.acBtnRebound.setOnClickListener {
            startActivity(Intent(this, ReboundActivity::class.java))
        }

        mDataBinding.acBtnFlow.setOnClickListener {
            startActivity(Intent(this, ConstraintLayoutFlowActivity::class.java))
        }

    }
}