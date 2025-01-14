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
import androidx.recyclerview.widget.ConcatAdapter
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

    private val mCenterAdapter = CenterAdapter()

    private val mAdapterHelper by lazy {
        QuickAdapterHelper.Builder(mCenterAdapter.apply {
                setOnItemClickListener{adapter,_,_->
                    adapter.add(3,"新增数据：${System.currentTimeMillis()}")
                }
            })
//            .setLeadingLoadStateAdapter(HeaderAdapter().apply {
//                setOnLeadingListener(object :LeadingLoadStateAdapter.OnLeadingListener{
//                    override fun onLoad() {
//
//                    }
//
//                    override fun isAllowLoading() = true
//                })
//            })
            .setTrailingLoadStateAdapter(FooterAdapter().apply {
                setOnLoadMoreListener(object :TrailingLoadStateAdapter.OnTrailingListener{
                    override fun onFailRetry() {
                        // 加载失败后，点击重试的操作，通常都是网络请求
                        Log.i("print_logs", "BaseSingleItemAdapterActivity::onFailRetry: ")
                    }

                    override fun onLoad() {
                        // 执行加载更多的操作，通常都是网络请求
                        Log.i("print_logs", "BaseSingleItemAdapterActivity::onLoad: ")
                    }

                    // 是否允许触发“加载更多”，通常情况下，下拉刷新的时候不允许进行加载更多
                    override fun isAllowLoading() :Boolean{
                        Log.i("print_logs", "BaseSingleItemAdapterActivity::isAllowLoading: ")
                        return true
                    }
                })
            })
            .setConfig(ConcatAdapter.Config.DEFAULT)
            .build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        mDataBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_base_single_item_adapter)
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
            adapter = mAdapterHelper.adapter
        }

        mCenterAdapter.submitList(mutableListOf("数据-1", "数据-2", "数据-3", "数据-4", "数据-5", "数据-6","数据-1", "数据-2", "数据-3", "数据-4", "数据-5", "数据-6","数据-1", "数据-2", "数据-3", "数据-4", "数据-5", "数据-6"))
    }

    /**
     * 头部
     */
    class HeaderAdapter : LeadingLoadStateAdapter<HeaderAdapter.HeaderViewHolder>() {
        inner class HeaderViewHolder(
            parent: ViewGroup,
            val binding: ItemSingleHeaderBinding = ItemSingleHeaderBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        ) : RecyclerView.ViewHolder(binding.root)

        override fun onBindViewHolder(holder: HeaderViewHolder, loadState: LoadState) {
            Log.i("print_logs", "HeaderAdapter::onBindViewHolder: $loadState")
        }

        override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): HeaderViewHolder {
            return HeaderViewHolder(parent)
        }
    }

    /**
     * 中间内容列表
     */
    inner class CenterAdapter : BaseQuickAdapter<String, CenterAdapter.CenterViewHolder>() {

        inner class CenterViewHolder(
            parent: ViewGroup,
            val binding: ItemOneBinding = ItemOneBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        ) : RecyclerView.ViewHolder(binding.root)

        override fun onBindViewHolder(holder: CenterViewHolder, position: Int, item: String?) {
            holder.binding.acTvInfo.text = item
        }

        override fun onCreateViewHolder(
            context: Context,
            parent: ViewGroup,
            viewType: Int
        ) = CenterViewHolder(parent)
    }

    /**
     * 底部
     */
    class FooterAdapter : TrailingLoadStateAdapter<FooterAdapter.FooterViewHolder>() {
        inner class FooterViewHolder(
            parent: ViewGroup,
            val binding: ItemFooterBinding = ItemFooterBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        ) : RecyclerView.ViewHolder(binding.root)

        override fun onBindViewHolder(holder: FooterViewHolder, loadState: LoadState) {
            Log.i("print_logs", "FooterAdapter::onBindViewHolder: $loadState")
            when (loadState) {
                is com.chad.library.adapter4.loadState.LoadState.Error -> {}
                is com.chad.library.adapter4.loadState.LoadState.Loading -> {}
                is com.chad.library.adapter4.loadState.LoadState.None -> {}
                is com.chad.library.adapter4.loadState.LoadState.NotLoading -> {}
                else ->{}
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): FooterViewHolder {
            return FooterViewHolder(parent)
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        mDataBinding.unbind()
    }
}