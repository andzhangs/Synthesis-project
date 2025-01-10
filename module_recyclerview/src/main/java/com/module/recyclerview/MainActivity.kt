package com.module.recyclerview

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.module.recyclerview.snap.R
import com.module.recyclerview.snap.databinding.ActivityMainBinding


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

        mDataBinding.acBtnDiffCallback.setOnClickListener {
            startActivity(Intent(this, DiffActivity::class.java))
        }

        mDataBinding.acBtnBaseQuickAdapter.setOnClickListener {
            startActivity(Intent(this, BaseQuickAdapterActivity::class.java))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mDataBinding.unbind()
    }
}