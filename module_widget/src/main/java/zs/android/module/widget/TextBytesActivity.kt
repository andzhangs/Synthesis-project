package zs.android.module.widget

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import zs.android.module.widget.databinding.ActivityTextBytesBinding
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

/**
 * 1、文字转二进制写入到.txt文件中
 * 2、将.txt文件中的二进制内容读取出来，并转换成文字
 */
class TextBytesActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var mDataBinding: ActivityTextBytesBinding

    private lateinit var filePath: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_text_bytes)

        filePath = "${externalCacheDir?.absoluteFile}${File.separator}Base64_CRLF.txt"

        mDataBinding.acBtnWrite.setOnClickListener(this)
        mDataBinding.acBtnRead.setOnClickListener(this)
    }

    private val txtType = Base64.URL_SAFE

    override fun onClick(v: View) {
        when (v.id) {
            mDataBinding.acBtnWrite.id -> {

                val txt = mDataBinding.acEtWrite.text.toString().toByteArray()
                val data = Base64.encodeToString(txt, txtType).toByteArray()

                try {
                    File(filePath).createNewFile()
                    val fos = FileOutputStream(filePath)
                    fos.write(data)
                    fos.close()
                    mDataBinding.acEtWrite.text = null
                    Toast.makeText(this, "写入成功！", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    e.printStackTrace()
                    if (BuildConfig.DEBUG) {
                        Log.e("print_logs", "写入失败: $e")
                    }
                    Toast.makeText(this, "写入失败!", Toast.LENGTH_SHORT).show()
                }
            }

            mDataBinding.acBtnRead.id -> {
                val file = File(filePath)

                if (file.exists()) {
                    try {
                        val fis = FileInputStream(file)
                        val fileData = ByteArray(file.length().toInt())
                        fis.read(fileData)
                        fis.close()
                        val decodeText = String(Base64.decode(fileData, txtType))
                        mDataBinding.acTvRead.text = decodeText
                        Toast.makeText(this, "读取成功！", Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        e.printStackTrace()
                        if (BuildConfig.DEBUG) {
                            Log.d("print_logs", "读取失败: $e")
                        }

                    }
                } else {
                    Toast.makeText(this, "文件不存在！", Toast.LENGTH_SHORT).show()
                }
            }

            else -> {}
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mDataBinding.unbind()
    }
}