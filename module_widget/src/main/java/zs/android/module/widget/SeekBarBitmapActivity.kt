package zs.android.module.widget

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import zs.android.module.widget.databinding.ActivitySeekBarBitmapBinding

class SeekBarBitmapActivity : AppCompatActivity() {

    private lateinit var mDataBinding: ActivitySeekBarBitmapBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_seek_bar_bitmap)
        mDataBinding.lifecycleOwner = this
        loadSingle()
        loadDouble()
    }

    /**
     * 单张图片
     */
    private fun loadSingle(){
        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.img_841)
        val width = bitmap.width
        val height = bitmap.height

        fun createSingleHalfTransparentBitmap(srcBitmap: Bitmap, newWidth: Int): Bitmap? {
            val newBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(newBitmap)

            canvas.drawBitmap(srcBitmap, 0f, 0f, null)

            val paint = Paint()
            paint.isAntiAlias = true

            paint.setXfermode(PorterDuffXfermode(PorterDuff.Mode.DST_IN))
            paint.color = Color.TRANSPARENT

            val rightHalf = Rect(newWidth, 0, width, height)
            canvas.drawRect(rightHalf, paint)
            return newBitmap
        }

        fun reloadBitmap(newWidth: Int) {
            val alphaBitmap = createSingleHalfTransparentBitmap(bitmap, newWidth)
            mDataBinding.acIvImg.setImageBitmap(alphaBitmap)
        }

        with(mDataBinding.acSeekBar) {
            max = width
            progress = width / 2

            //初始化
            reloadBitmap(progress)

            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    reloadBitmap(progress)
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {

                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {

                }
            })
        }
    }


    /**
     * 两张图片
     */
    private fun loadDouble(){
        val mLeftBitmap = BitmapFactory.decodeResource(resources, R.drawable.img_sky)
        val mRightBitmap = BitmapFactory.decodeResource(resources, R.drawable.img_girl)
        val mWidth = mLeftBitmap.width
        val mHeight = mLeftBitmap.height

        fun createDoubleHalfTransparentBitmap(leftSourceBitmap: Bitmap, rightSourceBitmap: Bitmap, newWidth: Int): Bitmap? {
            val resultBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(resultBitmap)
            if (newWidth == 0) {
                val newRightBitmap=Bitmap.createBitmap(rightSourceBitmap,newWidth,0,mWidth-newWidth,mHeight)
                canvas.drawBitmap(newRightBitmap, newWidth.toFloat(), 0f, null)
            }else if (newWidth == mWidth){
                val newLeftBitmap=Bitmap.createBitmap(leftSourceBitmap,0,0,newWidth,mHeight)
                canvas.drawBitmap(newLeftBitmap, 0f, 0f, null)
            }else{
                val newLeftBitmap=Bitmap.createBitmap(leftSourceBitmap,0,0,newWidth,mHeight)
                val newRightBitmap=Bitmap.createBitmap(rightSourceBitmap,newWidth,0,mWidth-newWidth,mHeight)
                canvas.drawBitmap(newLeftBitmap, 0f, 0f, null)
                canvas.drawBitmap(newRightBitmap, newWidth.toFloat(), 0f, null)
            }
            return resultBitmap
        }

        with(mDataBinding.acSeekBar2) {

            fun reloadBitmap(newWidth: Int) {
                val alphaBitmap = createDoubleHalfTransparentBitmap(mLeftBitmap,mRightBitmap, newWidth)
                mDataBinding.acIvImg2.setImageBitmap(alphaBitmap)
            }

            max = mWidth
            progress = mWidth / 2

            //初始化
            reloadBitmap(progress)

            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    reloadBitmap(progress)
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {

                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {

                }
            })
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mDataBinding.unbind()
    }

}