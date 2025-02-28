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
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import zs.android.module.widget.databinding.ActivitySeekBarBitmapBinding

class SeekBarBitmapActivity : AppCompatActivity(), SurfaceHolder.Callback {

    private lateinit var mDataBinding: ActivitySeekBarBitmapBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_seek_bar_bitmap)
        mDataBinding.lifecycleOwner = this
        loadSingle()
        loadDouble()

        mDataBinding.surfaceView.addBitmap(
            BitmapFactory.decodeResource(
                resources,
                R.drawable.img_girl
            )
        )

        mDataBinding.surfaceView.setOnClickListener {
            mDataBinding.surfaceView.addBitmap(
                BitmapFactory.decodeResource(
                    resources,
                    R.drawable.img_841
                )
            )
        }
    }

    /**
     * 单张图片
     */
    private fun loadSingle() {
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
    private fun loadDouble() {
        val mLeftBitmap = BitmapFactory.decodeResource(resources, R.drawable.img_sky)
        val mRightBitmap = BitmapFactory.decodeResource(resources, R.drawable.img_girl)
        val mWidth = mLeftBitmap.width
        val mHeight = mLeftBitmap.height

        with(mDataBinding.acSeekBar2) {

            fun reloadBitmap(newWidth: Int) {
//                val alphaBitmap = createDoubleHalfTransparentBitmap(mLeftBitmap,mRightBitmap, newWidth)

                val resultBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(resultBitmap)
                if (newWidth == 0) {
                    val newRightBitmap =
                        Bitmap.createBitmap(mRightBitmap, newWidth, 0, mWidth - newWidth, mHeight)
                    canvas.drawBitmap(newRightBitmap, newWidth.toFloat(), 0f, null)
                } else if (newWidth == mWidth) {
                    val newLeftBitmap = Bitmap.createBitmap(mLeftBitmap, 0, 0, newWidth, mHeight)
                    canvas.drawBitmap(newLeftBitmap, 0f, 0f, null)
                } else {
                    val newLeftBitmap = Bitmap.createBitmap(mLeftBitmap, 0, 0, newWidth, mHeight)
                    val newRightBitmap =
                        Bitmap.createBitmap(mRightBitmap, newWidth, 0, mWidth - newWidth, mHeight)
                    canvas.drawBitmap(newLeftBitmap, 0f, 0f, null)
                    canvas.drawBitmap(newRightBitmap, newWidth.toFloat(), 0f, null)
                }

                mDataBinding.acIvImg2.setImageBitmap(resultBitmap)
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

                    //根据进度计算游标位置
                    seekBar?.let {
                        val thumbOffsetX = it.thumb.bounds.left
                        val seekBarX = it.x

                        //计算要跟随的View的X坐标
                        val targetX = (thumbOffsetX - mDataBinding.acIvLine.width / 2).toFloat()

                        if (BuildConfig.DEBUG) {
                            Log.i("print_logs", "$thumbOffsetX, $seekBarX, $targetX")
                        }

                        //更新跟随View的位置
                        mDataBinding.acIvLine.x = targetX
                    }
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

    private var mSurfaceHolder: SurfaceHolder? = null
//    private var frame:Mat

    override fun surfaceCreated(holder: SurfaceHolder) {

    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {

    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {

    }

}