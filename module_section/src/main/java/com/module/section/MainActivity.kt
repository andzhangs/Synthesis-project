package com.module.section

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.module.section.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {


    private lateinit var mDataBinding: ActivityMainBinding
    private lateinit var mAdapter: MySectionQuickAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        with(mDataBinding.rvList) {
            this.layoutManager = GridLayoutManager(this@MainActivity, 4)
            mAdapter = MySectionQuickAdapter(mList = getNewData()).apply {
                getSelectedAllLiveData().observe(this@MainActivity) { isAllSelected ->
                    if (isAllSelected) {
                        mDataBinding.acBtnSelectAll.text = "取消全选"
                    } else {
                        mDataBinding.acBtnSelectAll.text = "全选"
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
            val section1 = MySection(isHeader = true, date = "2024年-7月-2日")
            add(section1)
            for (index in 1..8) {
                add(
                    MySection(
                        isHeader = false,
                        date = "2024年-7月-2日",
                        content = "打印：$index"
                    )
                )
            }

            val section2 = MySection(isHeader = true, date = "2024年-7月-1日")
            add(section2)

            for (index in 10..15) {
                add(
                    MySection(
                        isHeader = false,
                        date = "2024年-7月-1日",
                        content = "打印：$index"
                    )
                )
            }

            val section3 = MySection(isHeader = true, date = "2024年-6月-30日")
            add(section3)
            for (index in 17..30) {
                add(
                    MySection(
                        isHeader = false,
                        date = "2024年-6月-30日",
                        content = "打印：$index"
                    )
                )
            }

            val section4 = MySection(isHeader = true, date = "2024年-5月-30日")
            add(section4)
            for (index in 32..35) {
                add(
                    MySection(
                        isHeader = false,
                        date = "2024年-5月-30日",
                        content = "打印：$index"
                    )
                )
            }

            val section5 = MySection(isHeader = true, date = "2024年-3月-10日")
            add(section5)
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