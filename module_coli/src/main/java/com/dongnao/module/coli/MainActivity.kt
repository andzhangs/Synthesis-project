package com.dongnao.module.coli

import android.os.Bundle
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.dongnao.module.coli.databinding.ActivityMainBinding
import com.dongnao.module.coli.databinding.LayoutItemBinding
import com.dongnao.module.coli.model.hpiamge.ImageBean
import com.dongnao.module.coli.model.wanandroid.DataX

class MainActivity : AppCompatActivity() {

    private lateinit var mDataBinding: ActivityMainBinding
    private val mViewModel: MainViewModel by viewModels()

    private var mCheckedSourceWan = false
    private var mCheckedTypeWan = false

    private val mHPImageList = arrayListOf<ImageBean?>()
    private val mWanAndroidList = arrayListOf<DataX>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        mDataBinding.lifecycleOwner = this
        mDataBinding.vm = mViewModel

        with(mDataBinding.recyclerView) {
            layoutManager =
                LinearLayoutManager(this@MainActivity, RecyclerView.VERTICAL, false)
            mDataBinding.recyclerView.adapter = mAdapter
        }

        mViewModel.imageLiveData.observe(this) { bean ->
            val imgList = bean.images
            if (imgList!!.isNotEmpty()) {
                mHPImageList.clear()
            }
            mHPImageList.addAll(imgList)
            mAdapter.notifyDataSetChanged()
        }

        mViewModel.wanAndroidLiveData.observe(this) { bean ->
            bean.data?.datas?.also { list ->
                if (list.isNotEmpty()) {
                    mWanAndroidList.clear()
                }
                mWanAndroidList.addAll(list)
                mAdapter.notifyDataSetChanged()
            }
        }

        mDataBinding.switchRemoteOrLocal.setOnCheckedChangeListener { buttonView, isChecked ->
            buttonView.text = if (isChecked) "网络" else "本地"
            this.mCheckedSourceWan = isChecked
            updateUI()
        }

        mDataBinding.switchType.setOnCheckedChangeListener { buttonView, isChecked ->
            buttonView.text = if (isChecked) "玩安卓" else "图片"
            this.mCheckedTypeWan = isChecked
            updateUI()
        }

        mViewModel.getImageData(false)
    }

    private fun updateUI() {
        mDataBinding.recyclerView.adapter = mAdapter
        if (mCheckedTypeWan) {
            mViewModel.getWanAndroidData(mCheckedSourceWan)
        } else {
            mViewModel.getImageData(mCheckedSourceWan)
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

        override fun getItemCount() = if (mCheckedTypeWan) mWanAndroidList.size else mHPImageList.size

        override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
            if (mCheckedTypeWan) {
                holder.bindADesk(mWanAndroidList[position])
            } else {
                holder.bindImage(mHPImageList[position])
            }
        }
    }

    private inner class ItemViewHolder(private val itemBinding: LayoutItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bindImage(data: ImageBean?) {
            itemBinding.ifvImg.load(data?.getImageUrl())
            itemBinding.acTvTitle.text = data?.title
            itemBinding.acTvDesc.text = data?.copyright
            itemBinding.acTvBottom.text = data?.startdate
        }

        fun bindADesk(data: DataX) {
            itemBinding.ifvImg.load(data.envelopePic)
            itemBinding.acTvTitle.text = data.title
            itemBinding.acTvDesc.text = data.desc
            itemBinding.acTvBottom.text = data.author
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mDataBinding.unbind()
    }
}