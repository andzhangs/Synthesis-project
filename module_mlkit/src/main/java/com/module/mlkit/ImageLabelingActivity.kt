package com.module.mlkit

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.common.model.RemoteModelManager
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.TranslateRemoteModel
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import com.module.mlkit.databinding.ActivityImageLabelingBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.Executors

class ImageLabelingActivity : AppCompatActivity() {

    private lateinit var mDataBinding: ActivityImageLabelingBinding
    private var hasTranslateModel = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        mDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_image_labeling)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)

        val pickImageLauncher =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) {

                mDataBinding.acIvImg.setImageURI(it)

                it?.let {
                    val image = InputImage.fromFilePath(this@ImageLabelingActivity, it)
                    labeler.process(image).addOnSuccessListener { list ->
                        val stringBuilder = StringBuilder()
                        val mSize = list.size
                        list.forEachIndexed { index, imageLabel ->
                            val labelIndex = imageLabel.index
                            val text = imageLabel.text
                            val confidence = imageLabel.confidence

                            loadTranslate(text) { translateStr ->
                                stringBuilder.append("$labelIndex，$text($translateStr), $confidence")
                                    .append("\n")
                                if (index == mSize - 1) {
                                    mDataBinding.acTvInfo.text = stringBuilder.toString()
                                }
                            }
                        }
                    }.addOnFailureListener { e ->
                        if (BuildConfig.DEBUG) {
                            Log.e("print_logs", "获取失败: $e")
                        }
                    }.addOnCompleteListener {
                        if (BuildConfig.DEBUG) {
                            Log.d("print_logs", "获取图像标签完成: ")
                        }

                    }
                }
            }

        mDataBinding.acBtnGetImage.setOnClickListener {
            pickImageLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        lifecycle.addObserver(mTranslator)

        //获取存储在设备上的翻译模型
        RemoteModelManager.getInstance().getDownloadedModels(TranslateRemoteModel::class.java)
            .addOnSuccessListener { list ->
                list.forEach {
                    if (BuildConfig.DEBUG) {
                        Log.i("print_logs", "已下载翻译模型: ${it.language}")
                    }
                }
                if (list.size < 2) {
//                    Toast.makeText(this, "没有下载任何翻译模型", Toast.LENGTH_SHORT).show()
                    Toast.makeText(this, "模型下载中...", Toast.LENGTH_LONG).show()
                    downloadModel()
                } else {
                    hasTranslateModel = true
                }
            }.addOnFailureListener {
                if (BuildConfig.DEBUG) {
                    Log.e("print_logs", "模型已下载失败！")
                }
            }
    }

    /**
     * 翻译
     */
    private val mTranslator by lazy {
        val options = TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.ENGLISH)
            .setTargetLanguage(TranslateLanguage.CHINESE)
            .setExecutor(Executors.newSingleThreadExecutor())
            .build()
        Translation.getClient(options)
    }

    private val mConditions by lazy {
        DownloadConditions.Builder()
            .requireWifi()
            .build()
    }

    private fun loadTranslate(contentStr: String, block: (String) -> Unit) {
        fun toTranslate() {
            mTranslator.translate(contentStr)
                .addOnSuccessListener {
                    if (BuildConfig.DEBUG) {
                        Log.i("print_logs", "${Thread.currentThread().name} 翻译成功：$it")
                    }
                    block(it)
                }.addOnFailureListener {
                    if (BuildConfig.DEBUG) {
                        Log.e("print_logs", "翻译失败! $it")
                    }
                }
        }
        if (hasTranslateModel) {
            toTranslate()
        } else {
           downloadModel {
               Toast.makeText(this, "模型下载中...", Toast.LENGTH_LONG).show()
               toTranslate()
           }
        }
    }

    /**
     * 下载模型
     */
    private fun downloadModel(block: (() -> Unit)? = null) {
        mTranslator.downloadModelIfNeeded(mConditions)
            .addOnSuccessListener {
                if (BuildConfig.DEBUG) {
                    Log.i("print_logs", "模型下载成功！")
                }
                Toast.makeText(this, "模型下载成功！", Toast.LENGTH_SHORT).show()
                hasTranslateModel = true
                block?.invoke()
            }.addOnFailureListener {
                if (BuildConfig.DEBUG) {
                    Log.e("print_logs", "模型下载失败！$it")
                }
            }
    }
}