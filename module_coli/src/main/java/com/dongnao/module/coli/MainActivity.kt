package com.dongnao.module.coli

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import coil.load
import com.dongnao.module.coli.databinding.ActivityMainBinding
import com.dongnao.module.coli.databinding.LayoutItemBinding

class MainActivity : AppCompatActivity() {

    private lateinit var mDataBinding: ActivityMainBinding

    //add("https://api.isoyu.com/uploads/beibei/beibei_059$i.jpg")
    private val mList: ArrayList<String> by lazy {
        arrayListOf<String>().apply {
            for (i in 0..15) {
                add("https://img.xjh.me/random_img.php")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        mDataBinding.lifecycleOwner = this

        with(mDataBinding.recyclerView) {
            layoutManager = StaggeredGridLayoutManager(2, RecyclerView.VERTICAL)
            adapter = mAdapter
        }
    }

    private val mAdapter = object : RecyclerView.Adapter<ItemViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
            val itemBinding = DataBindingUtil.inflate<LayoutItemBinding>(
                layoutInflater,
                R.layout.layout_item,
                parent,
                false
            )
            return ItemViewHolder(itemBinding)
        }

        override fun getItemCount() = mList.size

        override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
            holder.bind(mList[position])
        }
    }

    private inner class ItemViewHolder(private val itemBinding: LayoutItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(imgUrl: String) {
            itemBinding.ifvImg.load(imgUrl)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mDataBinding.unbind()
    }
}