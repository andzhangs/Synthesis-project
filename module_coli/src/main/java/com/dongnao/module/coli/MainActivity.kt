package com.dongnao.module.coli

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.dongnao.module.coli.databinding.ActivityMainBinding
import com.dongnao.module.coli.databinding.LayoutItemBinding
import com.dongnao.module.coli.model.movie.DataBean
import com.dongnao.module.coli.model.wanandroid.DataX

class MainActivity : AppCompatActivity() {

    private lateinit var mDataBinding: ActivityMainBinding
    private val mViewModel: MainViewModel by viewModels()

    //add("https://api.isoyu.com/uploads/beibei/beibei_059$i.jpg")
//    private val mMovieList: ArrayList<String> by lazy {
//        arrayListOf<String>().apply {
//            for (i in 0..15) {
//                add("https://img.xjh.me/random_img.php")
//            }
//        }
//    }

    private var mCheckedSourceWan = false
    private var mCheckedTypeWan = false

    private val mMovieList = arrayListOf<DataBean>()
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

        mViewModel.movieLiveData.observe(this) { bean ->
            val imgList = bean.data
            if (imgList.isNotEmpty()) {
                mMovieList.clear()
            }
            mMovieList.addAll(imgList)
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
            buttonView.text = if (isChecked) "玩安卓" else "电影"
            this.mCheckedTypeWan = isChecked
            updateUI()
        }

        mViewModel.getMovieData(true)
    }

    private fun updateUI() {

        mDataBinding.recyclerView.adapter = mAdapter

        if (mCheckedTypeWan) {
            mViewModel.getWanAndroidData(mCheckedSourceWan)
        } else {
            mViewModel.getMovieData(mCheckedSourceWan)
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

        override fun getItemCount() = if (mCheckedTypeWan) mWanAndroidList.size else mMovieList.size

        override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
            if (mCheckedTypeWan) {
                holder.bindADesk(mWanAndroidList[position])
            } else {
                holder.bindMovie(mMovieList[position])
            }
        }
    }

    private inner class ItemViewHolder(private val itemBinding: LayoutItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bindMovie(data: DataBean) {
            itemBinding.ifvImg.load(data.info?.imgurl)
            itemBinding.acTvTitle.text = data.title
            itemBinding.acTvDesc.text = data.info?.yanyuan
            itemBinding.acTvBottom.text = data.info?.getMsg()
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