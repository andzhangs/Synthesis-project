package zs.android.app

import android.Manifest
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import zs.android.app.databinding.ActivityMainBinding
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var mDataBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        mDataBinding.lifecycleOwner = this


        mDataBinding.acBtnSetFont.setOnClickListener {

            mDataBinding.llRoot.removeAllViews()

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


            val result = IntTransformer().invoke(9)
            if (BuildConfig.DEBUG) {
                Log.i("print_logs", "IntTransformer: $result")
            }
        }

        val allFileLaunch =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == RESULT_OK) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        if (Environment.isExternalStorageManager()) {
                            loadJar()
                        } else {
                            Toast.makeText(this, "授权失败!", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

        val launch = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                loadJar()
            } else {
                Toast.makeText(this, "权限申请失败!", Toast.LENGTH_SHORT).show()
            }
        }

        mDataBinding.acBtnLoadJar.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    loadJar()
                } else {
                    val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                    intent.data = Uri.parse("package:${packageName}")
//                    startActivityForResult(intent, 1024)
                    allFileLaunch.launch(intent)
                }
            } else {
                launch.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }

        lifecycleScope.launch(Dispatchers.IO) {
            val info = resources.getString(R.string.test_format).format(12, 32)
            if (BuildConfig.DEBUG) {
                Log.d("print_logs", "string format: $info")
            }
        }

        mDataBinding.acIv.setOnClickListener {

            val bitmap = BitmapFactory.decodeResource(resources, R.drawable.img_ami)
            // 创建一个与源 Bitmap 大小相同的画布
            val croppedBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(croppedBitmap)


            //绘制半圆路径
            val path = Path()
            path.addArc(0f, 0f, bitmap.width.toFloat(), bitmap.height.toFloat(), 60f, 220f)
            canvas.clipPath(path)
            // 绘制源 Bitmap 到画布上
            canvas.drawBitmap(bitmap, 0f, 0f, null)
            mDataBinding.acIv.setImageBitmap(croppedBitmap)

            Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("byyourside://?data=value")
                startActivity(this)
            }
        }
    }

    private fun loadJar() {
        try {
            val clz = classLoader.loadClass("com.synthesis.modulejar.AppConfigs")
//            val clz=Class.forName("com.synthesis.modulejar.AppConfigs",true,classLoader)

            val instance = clz.newInstance()
            val method = clz.getMethod("getAppContent", String::class.java)
            method.invoke(instance, "访问方法：getAppContent")

            //获取私有方法
            val loadMethod = clz.getDeclaredMethod("load", String::class.java)
            //设置访问权限
            loadMethod.isAccessible = true
            loadMethod.invoke(instance, "私有方法【load】.")

            val appJarClazz = classLoader.loadClass("com.synthesis.modulejar.AppJar")
            val loadMethod2 =
                appJarClazz.getDeclaredMethod("toast", Context::class.java, String::class.java)
            loadMethod2.invoke(appJarClazz, this, "静态方法：load")
        } catch (e: Exception) {
            e.printStackTrace()
            if (BuildConfig.DEBUG) {
                Log.e("print_logs", "加载失败: $e")
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1024) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    loadJar()
                } else {
                    Toast.makeText(this, "授权失败！", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    /**
     * 使用实现函数类型作为接口的自定义类的实例：
     */
    class IntTransformer : (Int) -> Int {
        override operator fun invoke(p1: Int): Int {
            return p1 * 10
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mDataBinding.unbind()
    }
}