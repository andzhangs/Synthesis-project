package com.module.recyclerview

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.SeekBar
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

        mDataBinding.acBtnBaseDiffAdapter.setOnClickListener {
            startActivity(Intent(this, BaseDifferAdapterActivity::class.java))
        }

        mDataBinding.acBtnBaseSingleItemAdapter.setOnClickListener {
            startActivity(Intent(this, BaseSingleItemAdapterActivity::class.java))
        }

        mDataBinding.acBtnBaseMultiItemAdapter.setOnClickListener {
            startActivity(Intent(this, BaseMultiItemAdapterActivity::class.java))
        }

        mDataBinding.acBtnQuickAdapterHelper.setOnClickListener {
            startActivity(Intent(this, QuickAdapterHelperActivity::class.java))
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        mDataBinding.unbind()
    }
}