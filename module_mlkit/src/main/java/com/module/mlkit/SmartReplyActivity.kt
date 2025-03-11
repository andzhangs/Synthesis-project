package com.module.mlkit

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.common.model.RemoteModel
import com.google.mlkit.common.model.RemoteModelManager
import com.google.mlkit.nl.languageid.LanguageIdentification
import com.google.mlkit.nl.languageid.LanguageIdentificationOptions
import com.google.mlkit.nl.smartreply.SmartReply
import com.google.mlkit.nl.smartreply.SmartReplySuggestionResult
import com.google.mlkit.nl.smartreply.TextMessage
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.TranslateRemoteModel
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import com.module.mlkit.databinding.ActivitySmartReplyBinding
import com.module.mlkit.databinding.ItemSmartReplyBinding
import com.module.mlkit.databinding.ItemSmartSendBinding
import java.util.concurrent.Executors

class SmartReplyActivity : AppCompatActivity() {

    private lateinit var mDataBinding: ActivitySmartReplyBinding
    private val mList = mutableListOf<ItemData>()
    private val mConversation = mutableListOf<TextMessage>()
    private lateinit var mAdapter: ItemAdapter
    private var hasTranslateModel = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        mDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_smart_reply)

        // 设置窗口插入监听器
        ViewCompat.setOnApplyWindowInsetsListener(mDataBinding.main) { v, insets ->

            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            // 获取软键盘插入
            val imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime())

//            Log.i(
//                "print_logs",
//                "setOnApplyWindowInsetsListener: ${systemBars.bottom} ${imeInsets.bottom}"
//            )
            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                if (imeInsets.bottom == 0) systemBars.bottom else imeInsets.bottom
            )
            insets
        }

        initRv()
        sendContent()
    }

    private fun initRv() {
        with(mDataBinding.recycleView) {
            layoutManager =
                LinearLayoutManager(this@SmartReplyActivity, RecyclerView.VERTICAL, true)
            adapter = ItemAdapter(mList).also {
                mAdapter = it
            }
        }
    }

    private class ItemAdapter(val itemList: MutableList<ItemData>) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        companion object {
            class ReplyViewHolder(val replyBinding: ItemSmartReplyBinding) :
                RecyclerView.ViewHolder(replyBinding.root) {
                fun setContent(content: String) {
                    replyBinding.acTvReplyContent.text = content
                }
            }

            class SendViewHolder(val sendBinding: ItemSmartSendBinding) :
                RecyclerView.ViewHolder(sendBinding.root) {

                fun setContent(content: String) {
                    sendBinding.acTvSendContent.text = content
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return if (viewType == ItemData.TYPE_REPLY) {
                ReplyViewHolder(
                    DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.item_smart_reply,
                        parent,
                        false
                    )
                )
            } else {
                SendViewHolder(
                    DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.item_smart_send,
                        parent,
                        false
                    )
                )
            }
        }

        override fun getItemViewType(position: Int): Int {
            return itemList[position].itemType
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            when (holder) {
                is ReplyViewHolder -> {
                    holder.setContent(itemList[position].content)
                }

                is SendViewHolder -> {
                    holder.setContent(itemList[position].content)
                }

                else -> {}
            }
        }

        override fun getItemCount() = itemList.size
    }

    private fun sendContent() {
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

        mDataBinding.acBtnSend.setOnClickListener {
            val sendContent = mDataBinding.acEtContent.text.toString().trim()
            if (sendContent.isNotEmpty()) {
                mList.add(0, ItemData(itemType = ItemData.TYPE_SEND, content = sendContent))
                mConversation.add(
                    TextMessage.createForLocalUser(
                        sendContent,
                        System.currentTimeMillis()
                    )
                )
                mDataBinding.acEtContent.text = null
                mAdapter.notifyItemInserted(0)

                loadLanguageIdentification(sendContent)
                loadTranslate(sendContent)

                SmartReply.getClient()
                    .suggestReplies(mConversation)
                    .addOnSuccessListener { result ->
                        when (result.status) {
                            SmartReplySuggestionResult.STATUS_NOT_SUPPORTED_LANGUAGE -> {
                                Log.e("print_logs", "仅支持英文！")
                            }

                            SmartReplySuggestionResult.STATUS_SUCCESS -> {
                                val reply = StringBuilder()
                                result.suggestions.forEach {
                                    val replyText = it.text
                                    Log.i("print_logs", "getReply: $replyText")
                                    reply.append("$replyText ")
                                }
                                mList.add(
                                    0,
                                    ItemData(
                                        itemType = ItemData.TYPE_REPLY,
                                        content = reply.toString()
                                    )
                                )
                                mConversation.add(
                                    TextMessage.createForRemoteUser(
                                        reply.toString(),
                                        System.currentTimeMillis(),
                                        "1101"
                                    )
                                )
                                mAdapter.notifyItemInserted(0)
                            }

                            else -> {}
                        }
                    }.addOnFailureListener {
                        Log.e("print_logs", "恢复失败: $it")
                    }.addOnCompleteListener {
                        Log.d("print_logs", "回复完成: ")
                    }
            }
        }
    }

    /**
     * 语言识别
     */
    private fun loadLanguageIdentification(contentStr: String) {
        LanguageIdentification.getClient(
            LanguageIdentificationOptions.Builder()
                .setConfidenceThreshold(0.34f)
                .build()
        ).identifyPossibleLanguages(contentStr)
            .addOnSuccessListener { list ->
                list.forEach {
                    Log.i(
                        "print_logs",
                        "语言识别(BCP-47语言代码): ${it.languageTag}, ${it.confidence}"
                    )
                }
            }.addOnFailureListener {
                Log.e("print_logs", "语言识别失败: $it")
            }.addOnCompleteListener {
                Log.d("print_logs", "识别完成！")
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

    private fun loadTranslate(contentStr: String) {
        fun toTranslate() {
            mTranslator.translate(contentStr)
                .addOnSuccessListener {
                    if (BuildConfig.DEBUG) {
                        Log.i("print_logs", "翻译成功：$it")
                    }
                }.addOnFailureListener {
                    if (BuildConfig.DEBUG) {
                        Log.e("print_logs", "翻译失败! $it")
                    }
                }.addOnCompleteListener {
                    if (BuildConfig.DEBUG) {
                        Log.d("print_logs", "翻译完成！")
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

    private fun downloadModel(block: (() -> Unit)? = null) {
        mTranslator.downloadModelIfNeeded(mConditions)
            .addOnSuccessListener {
                if (BuildConfig.DEBUG) {
                    Log.i("print_logs", "模型下载完成！")
                }
                Toast.makeText(this, "模型下载完成！", Toast.LENGTH_SHORT).show()
                hasTranslateModel = true
                block?.invoke()
            }.addOnFailureListener {
                if (BuildConfig.DEBUG) {
                    Log.e("print_logs", "模型下载失败！$it")
                }
            }.addOnCompleteListener {
                if (BuildConfig.DEBUG) {
                    Log.d("print_logs", "模型下载完成！")
                }
            }
    }

    private data class ItemData(val itemType: Int, val content: String) {
        companion object {
            const val TYPE_REPLY = 0
            const val TYPE_SEND = 1
        }
    }
}