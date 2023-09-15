package com.module.media3

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.viewpager2.widget.ViewPager2
import com.module.media3.databinding.ActivityViewPager2Binding

class ViewPager2Activity : AppCompatActivity() {

    private lateinit var mDataBinding: ActivityViewPager2Binding
    private lateinit var mPickPlayFile: ActivityResultLauncher<PickVisualMediaRequest>


    private lateinit var mAdapter: MyVideoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_view_pager2)
        mPickPlayFile =
            registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(9)) { uris ->
                initViewPager(uris.toMutableList())
            }

        mDataBinding.acBtnGetFile.setOnClickListener {
            mPickPlayFile.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly))
        }
    }

    private fun initViewPager(list: MutableList<Uri>) {
        loadPlayer()

        with(mDataBinding.viewPager) {
            offscreenPageLimit = 5
            orientation=ViewPager2.ORIENTATION_VERTICAL
            mAdapter = MyVideoAdapter(this@ViewPager2Activity, this, list)
            adapter = mAdapter
            registerOnPageChangeCallback(mPagerChangeListener)
        }
    }

    private fun loadPlayer() {
        mDataBinding.acBtnPlay.setOnClickListener {
            mAdapter.play(mCurrentPosition)
        }

        mDataBinding.acBtnPause.setOnClickListener {
            mAdapter.pause(mCurrentPosition)
        }
    }

    private var mCurrentPosition = -1

    private val mPagerChangeListener = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            if (BuildConfig.DEBUG) {
                Log.i("print_logs", "onPageSelected: $position")
            }

            if (mCurrentPosition != position) {
                mAdapter.setPrepare(position)
                mAdapter.pause(position)
            }

            if (mCurrentPosition != -1) {
                mAdapter.stop(mCurrentPosition)
            }

            mCurrentPosition = position
        }
    }

    override fun onDestroy() {
        mDataBinding.viewPager.registerOnPageChangeCallback(mPagerChangeListener)
        mAdapter.releaseAll()
        super.onDestroy()
        mDataBinding.unbind()
    }
}