package com.module.recyclerview

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.util.Log
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

        var percent = 0.1f
        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.img_841)
        val width = bitmap.width
        val height = bitmap.height

        mDataBinding.acBtnCrop.setOnClickListener {

            Log.i("print_logs", "MainActivity::onCreate: $percent ${(width * percent).toInt()}")
            val cropBitmap = Bitmap.createBitmap(bitmap, 0, 0, (width * percent).toInt(), height)
            if (percent >= 1.0f) {
                percent = 0.1f
            } else {
                percent += 0.1f
            }
            mDataBinding.acIvImg.setImageBitmap(cropBitmap)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mDataBinding.unbind()
    }
}