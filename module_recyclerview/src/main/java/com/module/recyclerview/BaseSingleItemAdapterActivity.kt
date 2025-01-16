package com.module.recyclerview

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.ViewParent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.AdapterListUpdateCallback
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter4.BaseMultiItemAdapter
import com.chad.library.adapter4.BaseQuickAdapter
import com.chad.library.adapter4.BaseSingleItemAdapter
import com.chad.library.adapter4.QuickAdapterHelper
import com.chad.library.adapter4.layoutmanager.QuickGridLayoutManager
import com.chad.library.adapter4.loadState.LoadState
import com.chad.library.adapter4.loadState.leading.LeadingLoadStateAdapter
import com.chad.library.adapter4.loadState.trailing.TrailingLoadStateAdapter
import com.module.recyclerview.ext.addDividerDefault
import com.module.recyclerview.model.Entity
import com.module.recyclerview.model.MultiEntity
import com.module.recyclerview.snap.R
import com.module.recyclerview.snap.databinding.ActivityBaseMultiItemAdapterBinding
import com.module.recyclerview.snap.databinding.ActivityBaseSingleItemAdapterBinding
import com.module.recyclerview.snap.databinding.ItemContentBinding
import com.module.recyclerview.snap.databinding.ItemFooterBinding
import com.module.recyclerview.snap.databinding.ItemHeaderBinding
import com.module.recyclerview.snap.databinding.ItemOneBinding
import com.module.recyclerview.snap.databinding.ItemSingleHeaderBinding

class BaseSingleItemAdapterActivity : AppCompatActivity() {

    private lateinit var mDataBinding: ActivityBaseSingleItemAdapterBinding

    private val mAdapter =HeaderAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        mDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_base_single_item_adapter)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        with(mDataBinding.recyclerView) {
            //如果你使用 GridLayoutManager ，请切换至 QuickGridLayoutManager，否则空布局无法铺满
            layoutManager = LinearLayoutManager(
                this@BaseSingleItemAdapterActivity,
                RecyclerView.VERTICAL,
                false
            )
            addDividerDefault(1)
            adapter = mAdapter
        }
        mDataBinding.acBtnAdd.setOnClickListener {
            mAdapter.item="你好！${System.currentTimeMillis()}"
        }

        mDataBinding.acBtnUpdatePayload.setOnClickListener {
            mAdapter.setItem("来自Payload的更新","from")
        }
    }

    /**
     * 头部
     */
    class HeaderAdapter : BaseSingleItemAdapter<String,HeaderAdapter.HeaderViewHolder>() {
        inner class HeaderViewHolder(
            parent: ViewGroup,
            val binding: ItemSingleHeaderBinding = ItemSingleHeaderBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        ) : RecyclerView.ViewHolder(binding.root)

        override fun onBindViewHolder(holder: HeaderViewHolder, item: String?) {
            Log.i("print_logs", "HeaderAdapter::onBindViewHolder: 3 $item")
            holder.binding.acTvInfo.text=item
        }

        override fun onBindViewHolder(
            holder: HeaderViewHolder,
            item: String?,
            payloads: List<Any>
        ) {
            if (payloads.isNotEmpty()) {
                Log.i("print_logs", "HeaderAdapter::onBindViewHolder: 1")
                holder.binding.acTvInfo.text = payloads[0].toString()
            }
            Log.i("print_logs", "HeaderAdapter::onBindViewHolder: 2")

            super.onBindViewHolder(holder, item, payloads)
        }

        override fun onCreateViewHolder(
            context: Context,
            parent: ViewGroup,
            viewType: Int
        )            = HeaderViewHolder(parent)
    }

    override fun onDestroy() {
        super.onDestroy()
        mDataBinding.unbind()
    }
}