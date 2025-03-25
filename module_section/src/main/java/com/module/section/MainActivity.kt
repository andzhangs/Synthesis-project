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

    private val mOnBackPressedCallback :OnBackPressedCallback by lazy {
        // enable：true-拦截执行代码块；false-不拦截不执行代码块
        object :OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                if (BuildConfig.DEBUG) {
                    Log.i("print_logs", "MainActivity::handleOnBackPressed: ")
                }
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

                selectedAllLiveData.observe(this@MainActivity) { isAllSelected ->
                    mDataBinding.acBtnSelectAll.text = if (isAllSelected) "取消全选" else "全选"
                }

                showMultiLiveData.observe(this@MainActivity){
                    mDataBinding.acBtnSelectAll.isVisible = it
                    //返回键监听
                    if (it){
                        onBackPressedDispatcher.addCallback(this@MainActivity,mOnBackPressedCallback)
                    }else{
                        mOnBackPressedCallback.remove()
                    }
                }
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
        mDataBinding.acBtnSelectAll.setOnClickListener {
            mAdapter.selectAllOrNone()
        }
        mDataBinding.acBtnDelete.setOnClickListener {
            mAdapter.delete()
        }
        mDataBinding.acBtnRestore.setOnClickListener {
            mAdapter.setNewInstance(getNewData())
        }
    }

    private fun getNewData(): MutableList<MySection> {
        return mutableListOf<MySection>().apply {
            val isExit=this.filter { it.isHeader }.find { it.date == "2024年-7月-2日"}
            if (isExit == null) {
                add(MySection(isHeader = true, date = "2024年-7月-2日"))
            }
            for (index in 1..8) {
                add(
                    MySection(
                        isHeader = false,
                        date = "2024年-7月-2日",
                        content = "打印：$index"
                    )
                )
            }

            val isExit2=this.filter { it.isHeader }.find { it.date == "2024年-7月-1日"}
            if (isExit2==null) {
                add(MySection(isHeader = true, date = "2024年-7月-1日"))
            }
            for (index in 10..15) {
                add(
                    MySection(
                        isHeader = false,
                        date = "2024年-7月-1日",
                        content = "打印：$index"
                    )
                )
            }

            val isExit3=this.filter { it.isHeader }.find { it.date == "2024年-6月-30日"}
            if (isExit3==null) {
                add(MySection(isHeader = true, date = "2024年-6月-30日"))
            }
            for (index in 17..30) {
                add(
                    MySection(
                        isHeader = false,
                        date = "2024年-6月-30日",
                        content = "打印：$index"
                    )
                )
            }

            val isExit4=this.filter { it.isHeader }.find { it.date == "2024年-5月-3日"}
            if (isExit4 == null) {
                add(MySection(isHeader = true, date = "2024年-5月-3日"))
            }
            for (index in 32..35) {
                add(
                    MySection(
                        isHeader = false,
                        date = "2024年-5月-30日",
                        content = "打印：$index"
                    )
                )
            }

            val isExit5=this.filter { it.isHeader }.find { it.date == "2024年-3月-10日"}
            if (isExit5 == null) {
                add(MySection(isHeader = true, date = "2024年-3月-10日"))
            }
            for (index in 37..48) {
                add(
                    MySection(
                        isHeader = false,
                        date = "2024年-3月-10日",
                        content = "打印：$index"
                    )
                )
            }
        }
    }

    override fun onDestroy() {
        mDataBinding.unbind()
        super.onDestroy()
    }
}