package com.module.recyclerview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.AppCompatTextView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.module.recyclerview.snap.BuildConfig
import com.module.recyclerview.snap.R
import com.module.recyclerview.snap.databinding.ActivityConcatBinding
import com.module.recyclerview.snap.databinding.ItemOneBinding
import com.module.recyclerview.snap.databinding.ItemTwoBinding

class ConcatActivity : AppCompatActivity() {

    private lateinit var mDataBinding: ActivityConcatBinding
    private val mOneList = ArrayList<String>()
    private val mTwoList = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_concat)

        for (i in 0 until 5) {
            mOneList.add("Hello - $i")
            mTwoList.add("World : $i")
        }

        val oneAdapter = OneAdapter(mOneList)
        val twoAdapter = TwoAdapter(mTwoList)

        val concatAdapter = ConcatAdapter(oneAdapter, twoAdapter)

        with(mDataBinding.recyclerView) {
            layoutManager = LinearLayoutManager(this@ConcatActivity, RecyclerView.VERTICAL, false)
            adapter = concatAdapter
        }

        mDataBinding.acBtnAddOne.setOnClickListener {
            oneAdapter.addData("Hello ${System.currentTimeMillis()}")
        }

        mDataBinding.acBtnAddTwo.setOnClickListener {
            twoAdapter.addData("World ${System.currentTimeMillis()}")
        }

        mDataBinding.acTvTitle.setOnClickListener {
            Toast.makeText(this, "点击到了", Toast.LENGTH_SHORT).show()
        }
    }

    inner class CustomViewHolder(private val view: View) :
        RecyclerView.ViewHolder(view) {

        fun bind(info: String) {
            view.setOnClickListener {
                if (BuildConfig.DEBUG) {
                    //优先获取 absoluteAdapterPosition此值
                    Log.i("print_logs", "bind: $absoluteAdapterPosition， $bindingAdapterPosition, $layoutPosition, $oldPosition")
                }
            }

            view.findViewById<AppCompatTextView>(R.id.acTv_info).text = info
        }
    }

    private inner class OneAdapter(private val mList: ArrayList<String>) :
        RecyclerView.Adapter<CustomViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val view = DataBindingUtil.inflate<ItemOneBinding>(
                layoutInflater,
                R.layout.item_one,
                parent,
                false
            )
            return CustomViewHolder(view.root)
        }

        override fun getItemCount(): Int = mList.size

        override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
            holder.bind(mList[position])
        }

        fun addData(msg: String) {
            mList.add(msg)
            notifyItemInserted(mList.size - 1)
        }
    }

    private inner class TwoAdapter(private val mList: ArrayList<String>) :
        RecyclerView.Adapter<CustomViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val view = DataBindingUtil.inflate<ItemTwoBinding>(
                layoutInflater,
                R.layout.item_two,
                parent,
                false
            )
            return CustomViewHolder(view.root)
        }

        override fun getItemCount(): Int = mList.size

        override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
            holder.bind(mList[position])
        }

        fun addData(msg: String) {
            mList.add(msg)
            notifyItemInserted(mList.size - 1)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mDataBinding.unbind()
    }
}