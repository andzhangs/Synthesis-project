package com.module.jni

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.module.jni.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    companion object {
        init {
            System.loadLibrary("jni")
        }
    }

    private lateinit var mDataBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        mDataBinding.lifecycleOwner = this

        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.img_640)
        mDataBinding.acIvImg.setImageBitmap(bitmap)

        mDataBinding.acIvImg.setOnClickListener {
            mDataBinding.acIvImg.setImageBitmap(handleBitmap(bitmap))
        }

        var shapeViewSat=0f
        mDataBinding.acIvShape.setOnClickListener {
            val matrix = ColorMatrix().apply { setSaturation(shapeViewSat) }
            val filter = ColorMatrixColorFilter(matrix)
            mDataBinding.acIvShape.colorFilter = filter
            shapeViewSat=if (shapeViewSat == 0f) {
                1f
            }else{
                0f
            }
        }


        var filterViewSat=1f
        mDataBinding.acIfvImg.setOnClickListener {

            mDataBinding.acIfvImg.saturation = filterViewSat

            filterViewSat=if (filterViewSat == 0f) {
                1f
            }else{
                0f
            }
        }
    }

    private fun handleBitmap(bitmap: Bitmap): Bitmap {
        val bmp = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        nativeProcessBitmap(bitmap)
        if (BuildConfig.DEBUG) {
            Log.i("print_logs", "MainActivity::handleBitmap: ")
        }
        return bmp
    }

    /**
     * 黑白特效
     */
    external fun nativeProcessBitmap(bitmap: Bitmap)

    override fun onDestroy() {
        super.onDestroy()
        mDataBinding.unbind()
    }
}