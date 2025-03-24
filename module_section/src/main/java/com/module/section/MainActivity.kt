package com.module.section

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.module.section.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var mDataBinding: ActivityMainBinding
    private lateinit var mAdapter: MySectionQuickAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        mutableListOf<MySection>().apply {
            val section1 = MySection(isHeader = true, date = "2024年-7月-2日")
            section1.startIndex = 1
            section1.endIndex = 8
            add(section1)
            for (index in 1..8) {
                add(
                    MySection(
                        isHeader = false,
                        date = "2024年-7月-2日",
                        content = "打印：$index"
                    ).apply {
                        headerIndex = 0
                    })
            }

            val section2 = MySection(isHeader = true, date = "2024年-7月-1日")
            section2.startIndex = 10
            section2.endIndex = 15
            add(section2)

            for (index in 10..15) {
                add(
                    MySection(
                        isHeader = false,
                        date = "2024年-7月-1日",
                        content = "打印：$index"
                    ).apply {
                        headerIndex = 9
                    })
            }

            val section3 = MySection(isHeader = true, date = "2024年-6月-30日")
            section3.startIndex = 17
            section3.endIndex = 30
            add(section3)
            for (index in 17..30) {
                add(
                    MySection(
                        isHeader = false,
                        date = "2024年-6月-30日",
                        content = "打印：$index"
                    ).apply {
                        headerIndex = 16
                    })
            }


            val section4 = MySection(isHeader = true, date = "2024年-5月-30日")
            section4.startIndex = 32
            section4.endIndex = 35
            add(section4)
            for (index in 32..35) {
                add(
                    MySection(
                        isHeader = false,
                        date = "2024年-5月-30日",
                        content = "打印：$index"
                    ).apply {
                        headerIndex = 31
                    })
            }

        }.also { mList ->
            with(mDataBinding.rvList) {
                this.layoutManager = GridLayoutManager(this@MainActivity, 4)
                mAdapter = MySectionQuickAdapter(R.layout.item_content, mList)
                adapter = mAdapter
            }
            with(mAdapter) {
                setOnItemChildLongClickListener { _, _, position ->
                    this.data[position].also {
                        if (!this.isMultiSelection) {
                            this.setMultipleSelect(true)
                        }
                    }
                    true
                }
                setOnItemChildClickListener { _, view, position ->
                    this.data[position].also {
                        if (this.isMultiSelection) {

                            when (view.id) {
                                R.id.acIv_select_all -> {  //当前组全选
                                    mAdapter.setGroupSelectAll(position)
                                }

                                R.id.acIv_img -> {  //点击图片选择
                                    mAdapter.setItemSelect(position)
                                }

                                R.id.acIv_select -> {  //点击选中按钮
                                    mAdapter.setItemSelect(position)
                                }

                                else -> {}
                            }
                        } else {
                            Toast.makeText(
                                this@MainActivity,
                                "点击：${it.content}",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    }
                }
            }
        }
        clickListener()
    }

    private fun clickListener() {
        mDataBinding.acBtnSelectAll.setOnClickListener {

            mAdapter.selectAll { isAllSelected ->
                if (isAllSelected) {
                    mDataBinding.acBtnSelectAll.text = "取消全选"
                } else {
                    mDataBinding.acBtnSelectAll.text = "全选"
                }
            }
        }
        mDataBinding.acBtnDelete.setOnClickListener {
            mAdapter.delete{ isAllSelected ->
                mDataBinding.acBtnSelectAll.text = "全选"
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mDataBinding.unbind()
    }
}