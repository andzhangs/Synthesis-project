package zs.android.app

import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import zs.android.app.databinding.ActivityMainBinding
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var mDataBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        mDataBinding.lifecycleOwner = this

        mDataBinding.acBtnSetFont.setOnClickListener {

            if (mDataBinding.llRoot.childCount != 0){
                mDataBinding.llRoot.removeAllViews()
            }

            val folderPath = "${getExternalFilesDir("")?.absolutePath}${File.separator}fonts"

            val fontFolder = File(folderPath)
            if (fontFolder.isDirectory) {
                fontFolder.listFiles()?.forEach {
                    AppCompatTextView(this).apply {
                        this.width = ViewGroup.LayoutParams.MATCH_PARENT
                        this.width = ViewGroup.LayoutParams.WRAP_CONTENT
                        this.textSize = 20f
                        this.text = "字体：${it.name}"
                        this.typeface = Typeface.createFromFile(it)

                        mDataBinding.llRoot.addView(this)
                    }
                }
            }

            AppCompatTextView(this).apply {
                this.width = ViewGroup.LayoutParams.MATCH_PARENT
                this.width = ViewGroup.LayoutParams.WRAP_CONTENT
                this.textSize = 20f
                this.text = "字体：Roboto_Spell.ttf"
                this.typeface = ResourcesCompat.getFont(this@MainActivity, R.font.roboto_spell)

                mDataBinding.llRoot.addView(this)
            }


            val result=IntTransformer().invoke(9)
            if (BuildConfig.DEBUG) {
                Log.i("print_logs", "IntTransformer: $result")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mDataBinding.unbind()
    }

    /**
     * 使用实现函数类型作为接口的自定义类的实例：
     */
    class IntTransformer : (Int) -> Int {
        override operator fun invoke(p1: Int): Int {
            return p1 * 10
        }
    }
}