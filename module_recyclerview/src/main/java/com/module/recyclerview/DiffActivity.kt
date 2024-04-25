package com.module.recyclerview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.module.recyclerview.snap.R
import com.module.recyclerview.snap.databinding.ActivityDiffBinding
import com.module.recyclerview.snap.databinding.ItemDiffCallbackLayoutBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.Serializable

class DiffActivity : AppCompatActivity() {

    companion object {


        data class ItemModel(val title: String, val info: String) : Serializable

        class ItemViewHolder(private val itemBinding: ItemDiffCallbackLayoutBinding) :
            RecyclerView.ViewHolder(itemBinding.root) {

            fun bind(model: ItemModel) {
                itemBinding.textTitle = model.title
                itemBinding.textInfo = model.info
                itemBinding.executePendingBindings()
            }
        }

        class ListDiffCallback(
            private val oldList: ArrayList<ItemModel>,
            private val newList: ArrayList<ItemModel>
        ) : DiffUtil.Callback() {
            override fun getOldListSize() = oldList.size

            override fun getNewListSize() = newList.size

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return oldList[oldItemPosition].title == newList[newItemPosition].title
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return oldList[oldItemPosition] == newList[newItemPosition]
            }
        }
    }


    private lateinit var mDataBinding: ActivityDiffBinding
    private val mList = arrayListOf(
        ItemModel(title = "你好", info = "哎呀"),
        ItemModel(title = "哈好的", info = "娃儿而"),
        ItemModel(title = "出详细", info = "燕塘乳业"),
        ItemModel(title = "去玩儿", info = "我饿请问"),
        ItemModel(title = "积极", info = "可以可")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_diff)
        mDataBinding.lifecycleOwner = this

        with(mDataBinding.recyclerView) {
            layoutManager = LinearLayoutManager(this@DiffActivity, RecyclerView.VERTICAL, false)
            mAdapter.setData(mList)
            adapter = mAdapter
        }
    }

    fun onAdd(view: View) {
        lifecycleScope.launch {
            mAdapter.updateItem(
                arrayListOf(
                    ItemModel(title = "新增-1", info = "哈哈哈-1"),
                    ItemModel(title = "新增-2", info = "哈哈哈-2"),
                    ItemModel(title = "新增-3", info = "哈哈哈-3")
                )
            )
        }
    }

    fun onUpdate(view: View) {
        lifecycleScope.launch {
            mAdapter.updateItem(
                arrayListOf(
                    ItemModel(title = "你好", info = "哎呀"),
                    ItemModel(title = "哈好的", info = "娃儿而"),
                    ItemModel(title = "出详细", info = "燕塘乳业"),
                    ItemModel(title = "去玩儿", info = "我饿请问"),
                    ItemModel(title = "积极", info = "可以可")
                )
            )
        }
    }

    fun onDelete(view: View) {
        lifecycleScope.launch {
            mAdapter.updateItem(
                arrayListOf(
                    ItemModel(title = "哈好的", info = "娃儿而")
                )
            )
        }
    }

    private val mAdapter = object : RecyclerView.Adapter<ItemViewHolder>() {

        private var mItemList = arrayListOf<ItemModel>()

        fun setData(list: List<ItemModel>) {
            mItemList = ArrayList(list)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
            val binding = DataBindingUtil.inflate<ItemDiffCallbackLayoutBinding>(
                LayoutInflater.from(this@DiffActivity),
                R.layout.item_diff_callback_layout,
                parent,
                false
            )
            return ItemViewHolder(binding)
        }

        override fun getItemCount() = mItemList.size

        override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
            holder.bind(mItemList[position])
        }

        suspend fun updateItem(newList: ArrayList<ItemModel>) {
            withContext(Dispatchers.IO){
                val diffCallback = ListDiffCallback(mItemList, newList)
                val diffResult = DiffUtil.calculateDiff(diffCallback)
                mItemList = newList
                diffResult
            }.also {
                it.dispatchUpdatesTo(this)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mDataBinding.unbind()
    }
}