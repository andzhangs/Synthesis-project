package com.module.section

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.LayoutInflater.Factory2
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.LayoutInflaterCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.module.section.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {


    private lateinit var mDataBinding: ActivityMainBinding
    private lateinit var mAdapter: MySectionQuickAdapter

    private val mOnBackPressedCallback: OnBackPressedCallback by lazy {
        // enable：true-拦截执行代码块；false-不拦截不执行代码块
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                mAdapter.setMultiSelection(false)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        with(mDataBinding.rvList) {
            this.layoutManager = GridLayoutManager(this@MainActivity, 4)
            mAdapter = MySectionQuickAdapter(mList = getNewData()).apply {
                //监听是否全选了
                selectedAllLiveData.observe(this@MainActivity) { isAllSelected ->
                    mDataBinding.acBtnSelectAll.text = if (isAllSelected) "取消全选" else "全选"
                }

                //监听是否开启多选了
                showMultiLiveData.observe(this@MainActivity) {
                    mDataBinding.acBtnSelectAll.isVisible = it
                    //返回键监听
                    if (it) {
                        onBackPressedDispatcher.addCallback(
                            this@MainActivity,
                            mOnBackPressedCallback
                        )
                    } else {
                        mOnBackPressedCallback.remove()
                    }
                }
            }.apply {
                registerAdapterDataObserver(object :RecyclerView.AdapterDataObserver(){
                    //调用了调用了 notifyDataSetChanged()才触发
                    override fun onChanged() {
                        super.onChanged()
                        if (BuildConfig.DEBUG) {
                            Log.i("print_logs", "MainActivity::onChanged: ")
                        }
                    }

                    override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
                        super.onItemRangeChanged(positionStart, itemCount)
                        if (BuildConfig.DEBUG) {
                            Log.i("print_logs", "MainActivity::onItemRangeChanged: ")
                        }
                    }

                    override fun onItemRangeChanged(
                        positionStart: Int,
                        itemCount: Int,
                        payload: Any?
                    ) {
                        super.onItemRangeChanged(positionStart, itemCount, payload)
                        if (BuildConfig.DEBUG) {
                            Log.i("print_logs", "MainActivity::onItemRangeChanged: ")
                        }
                    }

                    override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                        super.onItemRangeInserted(positionStart, itemCount)
                        if (BuildConfig.DEBUG) {
                            Log.d("print_logs", "MainActivity::onItemRangeInserted: ")
                        }
                    }

                    override fun onItemRangeMoved(
                        fromPosition: Int,
                        toPosition: Int,
                        itemCount: Int
                    ) {
                        super.onItemRangeMoved(fromPosition, toPosition, itemCount)
                        if (BuildConfig.DEBUG) {
                            Log.e("print_logs", "MainActivity::onItemRangeMoved: ")
                        }
                    }

                    override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
                        super.onItemRangeRemoved(positionStart, itemCount)
                        if (BuildConfig.DEBUG) {
                            Log.e("print_logs", "MainActivity::onItemRangeRemoved: ")
                        }
                    }

                    override fun onStateRestorationPolicyChanged() {
                        super.onStateRestorationPolicyChanged()
                        if (BuildConfig.DEBUG) {
                            Log.i("print_logs", "MainActivity::onStateRestorationPolicyChanged: ")
                        }
                    }
                })
            }
            adapter = mAdapter

            addOnItemTouchListener(mAdapter.dragSelectTouchListener)

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    (recyclerView.layoutManager as? GridLayoutManager)?.also { layoutManager ->
                        val firstVisibleItemPosition =
                            layoutManager.findFirstVisibleItemPosition()
                        if (mAdapter.data.isNotEmpty()) {
                            val mySection = mAdapter.data[firstVisibleItemPosition]
                            mDataBinding.includeHeader.acTvDate.text = mySection.date
                        } else {

                        }
                        mDataBinding.includeHeader.clHeader.isVisible =
                            mAdapter.data.isNotEmpty()
                    }
                }
            })
        }
        clickListener()
    }

    private fun clickListener() {
//        mDataBinding.acBtnAdd.setOnClickListener {
//            mAdapter.addData(0,MySection(
//                isHeader = false,
//                date = "2024年-7月-2日",
//                content = "打印：新增"
//            ))
//        }
//        mDataBinding.acBtnRemove.setOnClickListener {
//            mAdapter.removeAt(0)
//        }

        mDataBinding.acBtnSelectAll.setOnClickListener {
            mAdapter.selectAllOrNone()
        }
        mDataBinding.acBtnDelete.setOnClickListener {
            mAdapter.delete()
        }
        mDataBinding.acBtnRestore.setOnClickListener {
            mDataBinding.includeHeader.clHeader.isVisible = true
            mAdapter.setNewInstance(getNewData())
        }
    }

    private fun getNewData(): MutableList<MySection> {
        return mutableListOf<MySection>().apply {
            val date1 = "2024年-7月-2日"
            val isExit = this.filter { it.isHeader }.find { it.date == date1 }
            if (isExit == null) {
                add(MySection(isHeader = true, date = date1))
            }
            for (index in 0..8) {
                add(
                    MySection(
                        isHeader = false,
                        date = date1,
                        content = "打印：$index"
                    )
                )
            }

            val date2 = "2024年-7月-1日"
            val isExit2 = this.filter { it.isHeader }.find { it.date == date2 }
            if (isExit2 == null) {
                add(MySection(isHeader = true, date = date2))
            }
            for (index in 10..15) {
                add(
                    MySection(
                        isHeader = false,
                        date = date2,
                        content = "打印：$index"
                    )
                )
            }

            val date3 = "2024年-6月-30日"
            val isExit3 = this.filter { it.isHeader }.find { it.date == date3 }
            if (isExit3 == null) {
                add(MySection(isHeader = true, date = date3))
            }
            for (index in 17..30) {
                add(
                    MySection(
                        isHeader = false,
                        date = date3,
                        content = "打印：$index"
                    )
                )
            }

            val date4 = "2024年-5月-3日"
            val isExit4 = this.filter { it.isHeader }.find { it.date == date4 }
            if (isExit4 == null) {
                add(MySection(isHeader = true, date = date4))
            }
            for (index in 32..35) {
                add(
                    MySection(
                        isHeader = false,
                        date = date4,
                        content = "打印：$index"
                    )
                )
            }

            val date5 = "2024年-3月-10日"
            val isExit5 = this.filter { it.isHeader }.find { it.date == date5 }
            if (isExit5 == null) {
                add(MySection(isHeader = true, date = date5))
            }
            for (index in 37..48) {
                add(
                    MySection(
                        isHeader = false,
                        date = date5,
                        content = "打印：$index"
                    )
                )
            }
        }.apply {
            if (this[0].isHeader) {
                removeAt(0)
            }
        }
    }

    override fun onDestroy() {
        mDataBinding.unbind()
        super.onDestroy()
    }
}