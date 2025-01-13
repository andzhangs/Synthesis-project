package com.module.recyclerview

import android.content.Context
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter4.BaseDifferAdapter
import com.chad.library.adapter4.BaseQuickAdapter
import com.chad.library.adapter4.layoutmanager.QuickGridLayoutManager
import com.chad.library.adapter4.util.setOnDebouncedItemClick
import com.module.recyclerview.ext.addDividerDefault
import com.module.recyclerview.model.Entity
import com.module.recyclerview.snap.R
import com.module.recyclerview.snap.databinding.ActivityBaseDifferAdapterBinding
import com.module.recyclerview.snap.databinding.ItemDiffBinding
import com.module.recyclerview.snap.databinding.ItemOneBinding

class BaseDifferAdapterActivity : AppCompatActivity() {

    private lateinit var mDataBinding: ActivityBaseDifferAdapterBinding
    private val mAdapter by lazy { TestAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        mDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_base_differ_adapter)
        mDataBinding.lifecycleOwner = this
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        with(mAdapter){
            setOnDebouncedItemClick{_,_,_->
                mAdapter.submitList(null)
            }
            //是否使用空布局
            isStateViewEnable=true
            setStateViewLayout(this@BaseDifferAdapterActivity,R.layout.layout_empty_view)
        }

        with(mDataBinding.recyclerView) {
            //如果你使用 GridLayoutManager ，请切换至 QuickGridLayoutManager，否则空布局无法铺满
            layoutManager = QuickGridLayoutManager(this@BaseDifferAdapterActivity,4, RecyclerView.VERTICAL, false)
            addDividerDefault(2)
            adapter = mAdapter
        }

        val mList= mutableListOf<Entity>().apply {
            for (i in 1..10) {
                add(Entity(content = "数据-$i"))
            }
        }
        mAdapter.submitList(mList)
    }

    inner class TestAdapter :
        BaseDifferAdapter<Entity, TestAdapter.TestViewHolder>(diffCallback = object : DiffUtil.ItemCallback<Entity>() {
            override fun areContentsTheSame(oldItem: Entity, newItem: Entity): Boolean {
                return oldItem.content == newItem.content
            }

            override fun areItemsTheSame(oldItem: Entity, newItem: Entity): Boolean {
                return oldItem == newItem
            }
        }) {
        inner class TestViewHolder(
            parent: ViewGroup,
            val binding: ItemDiffBinding = ItemDiffBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        ) : RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(
            context: Context,
            parent: ViewGroup,
            viewType: Int
        ) = TestViewHolder(parent)

        override fun onBindViewHolder(holder: TestViewHolder, position: Int, item: Entity?) {
            holder.binding.entity = item
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mDataBinding.unbind()
    }
}