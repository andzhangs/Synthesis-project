package com.module.recyclerview

import android.content.Context
import android.os.Bundle
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
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter4.BaseMultiItemAdapter
import com.chad.library.adapter4.layoutmanager.QuickGridLayoutManager
import com.module.recyclerview.ext.addDividerDefault
import com.module.recyclerview.model.Entity
import com.module.recyclerview.model.MultiEntity
import com.module.recyclerview.snap.R
import com.module.recyclerview.snap.databinding.ActivityBaseMultiItemAdapterBinding
import com.module.recyclerview.snap.databinding.ItemContentBinding
import com.module.recyclerview.snap.databinding.ItemHeaderBinding

class BaseMultiItemAdapterActivity : AppCompatActivity() {

    private lateinit var mDataBinding: ActivityBaseMultiItemAdapterBinding

    private val mAdapter = TestAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        mDataBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_base_multi_item_adapter)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        with(mDataBinding.recyclerView) {
            //如果你使用 GridLayoutManager ，请切换至 QuickGridLayoutManager，否则空布局无法铺满
            layoutManager = QuickGridLayoutManager(
                this@BaseMultiItemAdapterActivity,
                4,
                RecyclerView.VERTICAL,
                false
            )
            addDividerDefault(1)
            adapter = mAdapter
        }

        val mList = mutableListOf(
            MultiEntity(header = "头部-1"),
            MultiEntity(content = "数据-1"),
            MultiEntity(content = "数据-2"),
            MultiEntity(content = "数据-3"),
            MultiEntity(header = "头部-2"),
            MultiEntity(content = "数据-4"),
            MultiEntity(content = "数据-5"),
            MultiEntity(content = "数据-6"),
            MultiEntity(content = "数据-7")
        )
        mAdapter.submitList(mList)
    }

    class TestAdapter : BaseMultiItemAdapter<MultiEntity>() {
        companion object {
            const val TYPE_ONE = 1
            const val TYPE_TWO = 2
        }

        class HeaderViewHolder(
            parent: ViewGroup,
            val mItemHeaderBinding: ItemHeaderBinding = ItemHeaderBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        ) : RecyclerView.ViewHolder(mItemHeaderBinding.root)

        class ContentViewHolder(
            parent: ViewGroup,
            val mItemContentBinding: ItemContentBinding = ItemContentBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
            )
        ) : RecyclerView.ViewHolder(mItemContentBinding.root)

        init {
            addItemType(TYPE_ONE,
                object : OnMultiItemAdapterListener<MultiEntity, HeaderViewHolder> {
                    override fun onCreate(
                        context: Context,
                        parent: ViewGroup,
                        viewType: Int
                    ) = HeaderViewHolder(parent)

                    override fun onBind(
                        holder: HeaderViewHolder,
                        position: Int,
                        item: MultiEntity?
                    ) {
                        holder.mItemHeaderBinding.entity = item
                    }

                    override fun isFullSpanItem(itemType: Int): Boolean {
                        // 使用GridLayoutManager时，此类型的 item 是否是满跨度
                        return true
                    }
                }).addItemType(TYPE_TWO,
                object : OnMultiItemAdapterListener<MultiEntity, ContentViewHolder> {
                    override fun onCreate(
                        context: Context,
                        parent: ViewGroup,
                        viewType: Int
                    ) = ContentViewHolder(parent)

                    override fun onBind(
                        holder: ContentViewHolder,
                        position: Int,
                        item: MultiEntity?
                    ) {
                        holder.mItemContentBinding.entity = item
                    }
                }).onItemViewType { position, list ->
                if (list[position].header.isNotEmpty()) {
                    TYPE_ONE
                } else {
                    TYPE_TWO
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mDataBinding.unbind()
    }
}