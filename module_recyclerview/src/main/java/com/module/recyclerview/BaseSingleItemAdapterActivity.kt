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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter4.BaseMultiItemAdapter
import com.chad.library.adapter4.BaseQuickAdapter
import com.chad.library.adapter4.BaseSingleItemAdapter
import com.chad.library.adapter4.layoutmanager.QuickGridLayoutManager
import com.module.recyclerview.ext.addDividerDefault
import com.module.recyclerview.model.Entity
import com.module.recyclerview.model.MultiEntity
import com.module.recyclerview.snap.R
import com.module.recyclerview.snap.databinding.ActivityBaseMultiItemAdapterBinding
import com.module.recyclerview.snap.databinding.ActivityBaseSingleItemAdapterBinding
import com.module.recyclerview.snap.databinding.ItemContentBinding
import com.module.recyclerview.snap.databinding.ItemHeaderBinding
import com.module.recyclerview.snap.databinding.ItemOneBinding

class BaseSingleItemAdapterActivity : AppCompatActivity() {

    private lateinit var mDataBinding: ActivityBaseSingleItemAdapterBinding

    private val mHeaderAdapter=HeaderAdapter()
    private val mAdapter = ContentAdapter()

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
            adapter = mAdapter
        }

        mAdapter.submitList(mutableListOf("数据-1", "数据-2", "数据-3", "数据-4", "数据-5", "数据-6"))


    }


    class HeaderAdapter : BaseSingleItemAdapter<Entity, HeaderAdapter.HeaderViewHolder>() {
        inner class HeaderViewHolder(
            parent: ViewGroup,
            val binding: ItemHeaderBinding = ItemHeaderBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        ) : RecyclerView.ViewHolder(binding.root)

        override fun onBindViewHolder(holder: HeaderViewHolder, item: Entity?) {
            
        }

        override fun onCreateViewHolder(
            context: Context,
            parent: ViewGroup,
            viewType: Int
        ) =HeaderViewHolder(parent)
    }

    inner class ContentAdapter : BaseQuickAdapter<String, ContentAdapter.TestViewHolder>() {

        inner class TestViewHolder(
            parent: ViewGroup,
            val binding: ItemOneBinding = ItemOneBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        ) : RecyclerView.ViewHolder(binding.root)

        override fun onBindViewHolder(holder: TestViewHolder, position: Int, item: String?) {
            holder.binding.acTvInfo.text = item
        }

        override fun onCreateViewHolder(
            context: Context,
            parent: ViewGroup,
            viewType: Int
        ) = TestViewHolder(parent)
    }

    override fun onDestroy() {
        super.onDestroy()
        mDataBinding.unbind()
    }
}