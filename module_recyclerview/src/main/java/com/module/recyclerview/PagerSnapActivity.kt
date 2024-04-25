package com.module.recyclerview

import android.Manifest
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.PowerManager
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.module.recyclerview.snap.R
import com.module.recyclerview.snap.databinding.ActivityPagerSnapBinding
import com.module.recyclerview.snap.databinding.LayoutImageBinding
import com.module.recyclerview.snap.databinding.LayoutVideoBinding
import java.io.File

class PagerSnapActivity : AppCompatActivity() {

    private lateinit var mDataBinding: ActivityPagerSnapBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_pager_snap)
        val launcher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (it[Manifest.permission.READ_MEDIA_IMAGES]!! && it[Manifest.permission.READ_MEDIA_VIDEO]!!) {
                        val parentFolder =
                            "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)}${File.separator}Camera"
                        File(parentFolder).also { file ->
                            if (file.exists()) {
                                file.listFiles()?.let { it1 ->
                                    if (it1.isNotEmpty()) {
                                        loadView(it1.toList() as ArrayList<File>)
                                    }
                                }
                            }
                        }
                    }
                }else{
                    if (it[Manifest.permission.READ_EXTERNAL_STORAGE]!! && it[Manifest.permission.WRITE_EXTERNAL_STORAGE]!!) {
                        val parentFolder =
                            "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)}${File.separator}Camera"
                        File(parentFolder).also { file ->
                            if (file.exists()) {
                                file.listFiles()
                                    ?.let { it1 -> loadView(it1.toList() as ArrayList<File>) }
                            }
                        }
                    }
                }
            }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            launcher.launch(
                arrayOf(
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_VIDEO
                )
            )
        } else {
            launcher.launch(
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            )
        }

        mDataBinding.acBtnJump.setOnClickListener {
            mAdapter.removeItem(0)
        }
    }

    private lateinit var mAdapter: FileAdapter
    private fun loadView(list: ArrayList<File>) {
        mAdapter = FileAdapter(list)
        with(mDataBinding.recyclerView) {
            layoutManager = LinearLayoutManager(
                this@PagerSnapActivity,
                LinearLayoutManager.HORIZONTAL,
                false
            ).apply {
                initialPrefetchItemCount = 10
            }
            PagerSnapHelper().attachToRecyclerView(this)
            adapter = mAdapter
        }
    }

    private class FileAdapter(val mList: ArrayList<File>) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        companion object {
            const val VIEW_IMAGE = 1
            const val VIEW_VIDEO = 2

            class ImageViewHolder(private val binding: LayoutImageBinding) :
                RecyclerView.ViewHolder(binding.root) {

                fun bind(fileUrl: String) {
                    Log.i("print_logs", "ImageViewHolder::bind: $fileUrl")
                    Glide.with(binding.acIvView.context)
                        .load(fileUrl)
                        .into(binding.acIvView)
                }
            }

            class VideoViewHolder(private val binding: LayoutVideoBinding) :
                RecyclerView.ViewHolder(binding.root) {

                fun bind(fileUrl: String) {
                        Log.i("print_logs", "VideoViewHolder::bind: $fileUrl")
                    val context = binding.videoView.context
                    binding.videoView.setVideoPath(fileUrl)
                    binding.videoView.requestFocus()
                    binding.videoView.setOnPreparedListener {
                        val videoWidth = it.videoWidth
                        val videoHeight = it.videoHeight
                        val videoProportion = videoWidth.toFloat() / videoHeight.toFloat()

                        val screenWidth = context.resources.displayMetrics.widthPixels
                        val screenHeight = context.resources.displayMetrics.heightPixels
                        val screenProportion = screenWidth.toFloat() / screenHeight.toFloat()

                        val layoutParams = binding.videoView.layoutParams
                        if (videoProportion > screenProportion) {
                            layoutParams.width = screenWidth
                            layoutParams.height = (screenWidth.toFloat() / videoProportion).toInt()
                        } else {
                            layoutParams.width = (videoProportion * screenHeight.toFloat()).toInt()
                            layoutParams.height = screenHeight
                        }
                        binding.videoView.layoutParams = layoutParams
                        it.seekTo(0)
                        //保持常亮
                        it.setScreenOnWhilePlaying(true)
                        it.setWakeMode(context, PowerManager.PARTIAL_WAKE_LOCK)
                    }
                }
            }
        }


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            return if (viewType == VIEW_IMAGE) {
                val imageBinding = DataBindingUtil.inflate<LayoutImageBinding>(
                    inflater,
                    R.layout.layout_image,
                    parent,
                    false
                )
                ImageViewHolder(imageBinding)
            } else {
                val videoBinding = DataBindingUtil.inflate<LayoutVideoBinding>(
                    inflater,
                    R.layout.layout_video,
                    parent,
                    false
                )
                VideoViewHolder(videoBinding)
            }
        }

        override fun getItemCount(): Int = mList.size

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val fileUrl = mList[position].absolutePath
            if (fileUrl.endsWith(".mp4")) {
                (holder as VideoViewHolder).bind(fileUrl)
            } else {
                (holder as ImageViewHolder).bind(fileUrl)
            }
        }

        override fun getItemViewType(position: Int): Int {
            return if (mList[position].absolutePath.endsWith(".mp4")) {
                VIEW_VIDEO
            } else {
                VIEW_IMAGE
            }
        }

        fun removeItem(position: Int) {
            mList.removeAt(position)
            notifyItemRemoved(position)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        mDataBinding.unbind()
    }
}