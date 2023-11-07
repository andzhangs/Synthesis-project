package zs.android.module.widget

import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.widget.AppCompatTextView
import androidx.databinding.DataBindingUtil
import com.tapadoo.alerter.Alerter
import zs.android.module.widget.databinding.ActivityAlertBinding

class AlertActivity : AppCompatActivity() {

    private lateinit var mDataBinding: ActivityAlertBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_alert)

        mDataBinding.acBtn1.setOnClickListener {
            Alerter.create(this)
                .setTitle("Hello World")
                .setText("I am Alerter")
                .show()
        }

        mDataBinding.acBtn2.setOnClickListener {
            Alerter.create(this)
                .setTitle("自定义背景色")
                .setText("I am Alerter")
                .setBackgroundColorRes(R.color.purple_200)
                .show()
        }

        mDataBinding.acBtn3.setOnClickListener {
            Alerter.create(this)
//                .setTitle("自定义图标")
                .setText("自定义图标")
                .setIcon(R.drawable.icon_selected)
                .setIconColorFilter(0)
                .setIconSize(R.dimen.dp_30)
                .show()
        }

        mDataBinding.acBtn4.setOnClickListener {
            Alerter.create(this)
                .setTitle("自定义显示时长")
                .setText("I am Alerter")
                .setDuration(1500)
                .show()
        }

        mDataBinding.acBtn5.setOnClickListener {
            Alerter.create(this)
                .setText("I am Alerter")
                .show()
        }

        mDataBinding.acBtn6.setOnClickListener {
            Alerter.create(this)
                .setTitle("添加点击事件")
                .setText("I am Alerter")
                .setDuration(3000)
                .setOnClickListener {
                    Toast.makeText(this, "点击成功！", Toast.LENGTH_SHORT).show()
                }
                .show()
        }


        mDataBinding.acBtn7.setOnClickListener {
            Alerter.create(this)
                .setTitle("过长文字")
                .setText("The alert scales to accommodate larger bodies of text. The alert scales to accommodate larger bodies of text. The alert scales to accommodate larger bodies of text. ")
                .show()
        }

        mDataBinding.acBtn8.setOnClickListener {
            Alerter.create(this)
                .setTitle("自定义进出动画")
                .setText("I am Alerter")
                .setEnterAnimation(com.tapadoo.alerter.R.anim.alerter_slide_in_from_left)
                .setExitAnimation(com.tapadoo.alerter.R.anim.alerter_slide_out_to_right)
                .show()
        }

        mDataBinding.acBtn9.setOnClickListener {
            Alerter.create(this)
                .setTitle("监听显示-隐藏")
                .setText("I am Alerter")
                .setOnShowListener { Toast.makeText(this, "显示了！", Toast.LENGTH_SHORT).show() }
                .setOnHideListener { Toast.makeText(this, "隐藏了！", Toast.LENGTH_SHORT).show() }
                .setDuration(2000)
                .show()
        }

        mDataBinding.acBtn10.setOnClickListener {
            Alerter.create(this)
                .setTitle("自定义字体")
                .setTitleAppearance(com.tapadoo.alerter.R.style.AlertTextAppearance_Title)
                .setTitleTypeface(Typeface.createFromAsset(assets, "Pacifico-Regular.ttf"))
                .setText("I am Alerter")
                .setTextAppearance(com.tapadoo.alerter.R.style.AlertTextAppearance_Text)
                .setTextTypeface(Typeface.createFromAsset(assets, "ScopeOne-Regular.ttf"))
                .show()
        }

        mDataBinding.acBtn11.setOnClickListener {
            Alerter.create(this)
                .setTitle("滑动删除")
                .setText("I am Alerter")
                .enableSwipeToDismiss()
                .show()
        }

        mDataBinding.acBtn12.setOnClickListener {
            Alerter.create(this)
                .setTitle("增加进度条")
                .setText("I am Alerter")
                .enableProgress(true)
                .show()
        }

        mDataBinding.acBtn13.setOnClickListener {
            Alerter.create(this)
                .setTitle("增加按钮")
                .setText("I am Alerter")
                .addButton("取消", com.tapadoo.alerter.R.style.AlertButton) {
                    Toast.makeText(this, "点击了：取消", Toast.LENGTH_SHORT).show()
                }
                .addButton("确定", com.tapadoo.alerter.R.style.AlertButton) {
                    Toast.makeText(this, "点击了：确认", Toast.LENGTH_SHORT).show()
                }
                .setDuration(3000)
                .show()
        }

        mDataBinding.acBtn14.setOnClickListener {
            Alerter.create(this, R.layout.layout_alerter_custom_view)
                .enableVibration(true)
                .setBackgroundColorRes(com.tapadoo.alerter.R.color.alerter_default_success_background).also { alerter ->
                    val textView = alerter.getLayoutContainer()
                        ?.findViewById(R.id.acTv_text) as AppCompatTextView
                    textView.text = "你好-哈哈哈哈"
                }.show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mDataBinding.unbind()
    }
}