package com.module.recyclerview.snap

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.PowerManager
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.module.recyclerview.snap.databinding.ActivityMainBinding
import com.module.recyclerview.snap.databinding.LayoutImageBinding
import com.module.recyclerview.snap.databinding.LayoutVideoBinding
import java.io.File


class MainActivity : AppCompatActivity() {
    private lateinit var mDataBinding: ActivityMainBinding

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        mDataBinding.acBtnPagerSnapAdapter.setOnClickListener {
            startActivity(Intent(this, PagerSnapActivity::class.java))
        }

        mDataBinding.acBtnConcatAdapter.setOnClickListener {
            startActivity(Intent(this, ConcatActivity::class.java))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mDataBinding.unbind()
    }
}