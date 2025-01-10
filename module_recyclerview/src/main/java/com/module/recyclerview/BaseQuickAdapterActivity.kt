package com.module.recyclerview

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.ViewParent
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.attrsense.ui.library.ext.addDividerDefault
import com.chad.library.adapter4.BaseQuickAdapter
import com.chad.library.adapter4.util.addOnDebouncedChildClick
import com.chad.library.adapter4.util.setOnDebouncedItemClick
import com.module.recyclerview.snap.R
import com.module.recyclerview.snap.databinding.ActivityBaseQuickAdapterBinding
import com.module.recyclerview.snap.databinding.ItemOneBinding

class BaseQuickAdapterActivity : AppCompatActivity() {

    private lateinit var mDataBinding: ActivityBaseQuickAdapterBinding

    private val mAdapter by lazy { TestAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        mDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_base_quick_adapter)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.cl_root)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        with(mAdapter) {

            /**
             * 方式一
             */
//            // item 去除点击抖动的扩展方法
//            setOnDebouncedItemClick{_,_,position->
//                Toast.makeText(this@BaseQuickAdapterActivity, "防抖：点击了Item【${position}】", Toast.LENGTH_SHORT).show()
//            }
//            // item Child 去除点击抖动的扩展方法
//            addOnDebouncedChildClick(R.id.acTv_info){_,_,position->
//                Toast.makeText(this@BaseQuickAdapterActivity, "防抖：点击子Item【${position}】的控件", Toast.LENGTH_SHORT).show()
//            }

            /**
             * 方式二
             */
            setOnItemClickListener{_,_,position->
                Toast.makeText(this@BaseQuickAdapterActivity, "点击了Item，当前索引${itemIndexOfFirst(items.get(position))}", Toast.LENGTH_SHORT).show()
            }
            addOnItemChildClickListener(R.id.acTv_info){_,_,position->

                Toast.makeText(this@BaseQuickAdapterActivity, "点击子Item【${position}】的控件, ${getItem(position)}", Toast.LENGTH_SHORT).show()
            }


            setOnItemLongClickListener{_,view,position->
                Toast.makeText(this@BaseQuickAdapterActivity, "长按了Item【${position}】", Toast.LENGTH_SHORT).show()
                true
            }
            addOnItemChildLongClickListener(R.id.acTv_info){_,view,position->
                when (view.id) {
                    R.id.acTv_info -> {
                        Toast.makeText(this@BaseQuickAdapterActivity, "长按子Item【${position}】的控件", Toast.LENGTH_SHORT).show()
                    }
                    else -> {}
                }
                true
            }
        }

        mDataBinding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@BaseQuickAdapterActivity,RecyclerView.VERTICAL,false)
            addDividerDefault(2)
            adapter=mAdapter
            mAdapter.submitList(listOf("0","1", "2", "3", "4", "5", "6", "7", "8", "9"))
        }

        mDataBinding.acBtnUpdate.setOnClickListener {
            mAdapter[1]="我是更新的"
        }
        mDataBinding.acBtnAdd.setOnClickListener {
            mAdapter.add("我是新增的数据")
        }
        mDataBinding.acBtnAdd1.setOnClickListener {
            mAdapter.add(1,"我是指定新增的")
        }
        mDataBinding.acBtnAddList.setOnClickListener {
            mAdapter.addAll(listOf("我是新增1处的数据集-1","我是新增1处的数据集-2"))
        }
        mDataBinding.acBtnAddList1.setOnClickListener {
            mAdapter.addAll(1,listOf("我是新增从1处的数据-1","我是新增从1处的数据-2"))
        }
        mDataBinding.acBtnAddDelete1.setOnClickListener {
            mAdapter.removeAt(1)
        }
        mDataBinding.acBtnAddSwap1With3.setOnClickListener {
            mAdapter.swap(1,3)
        }
    }


    inner class TestAdapter : BaseQuickAdapter<String, TestAdapter.TestViewHolder>() {

        inner class TestViewHolder(
            parent: ViewGroup,
            val binding: ItemOneBinding = ItemOneBinding.inflate(
                LayoutInflater.from(parent.context),parent,false
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