package zs.android.module.widget.widget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.HandlerThread
import android.util.AttributeSet
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import zs.android.module.widget.R
import java.util.LinkedList
import java.util.Queue

/**
 * @author zhangshuai
 * @date 2025/2/10 15:50
 * @description 自定义类描述
 */

class BitmapRenderingSurfaceView @JvmOverloads constructor(
    context: Context,
    attr: AttributeSet?,
    defStyleAttr: Int = 0
) : SurfaceView(context,attr,defStyleAttr),
    SurfaceHolder.Callback {
    private val mSurfaceHolder: SurfaceHolder = holder
    private var mRenderThread: RenderThread? = null
    private val mBitmapQueue: Queue<Bitmap> = LinkedList()

    init {
        mSurfaceHolder.addCallback(this)
    }

    // 向队列中添加 Bitmap
    fun addBitmap(bitmap: Bitmap) {
        synchronized(mBitmapQueue) {
            mBitmapQueue.add(bitmap)

            if (mRenderThread==null) {
                Log.i("print_logs", "addBitmap:  mRenderThread is null")

                mRenderThread = RenderThread(mSurfaceHolder)
                mRenderThread!!.start()
            }
        }
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        Log.i("print_logs", "BitmapRenderingSurfaceView::surfaceCreated: ")

//        mRenderThread = RenderThread(mSurfaceHolder)
//        mRenderThread!!.start()

    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        // 处理 Surface 大小变化的逻辑
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        Log.i("print_logs", "BitmapRenderingSurfaceView::surfaceDestroyed: ")
        var retry = true
        mRenderThread!!.setRunning(false)

        clearQueue()

        while (retry) {
            try {
                mRenderThread!!.join()
                retry = false
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
    }

    private inner class RenderThread(private val mHolder: SurfaceHolder) : Thread() {
        private var mIsRunning = true
        private val mPaint = Paint()

        fun setRunning(running: Boolean) {
            mIsRunning = running
        }

        override fun run() {
            while (mIsRunning) {
                var canvas: Canvas? = null
                try {
                    canvas = mHolder.lockCanvas()
                    if (canvas != null) {
                        synchronized(mBitmapQueue) {
                            // 从队列中取出 Bitmap 并绘制
                            if (!mBitmapQueue.isEmpty()) {
                                val bitmap = mBitmapQueue.poll()
                                if (bitmap != null) {
                                    Log.i("print_logs", "RenderThread::run: 非空")
                                    canvas.drawColor(Color.WHITE)
                                    canvas.drawBitmap(bitmap, 0f, 0f, mPaint)
                                    mHolder.unlockCanvasAndPost(canvas)
                                    mIsRunning=false
                                    this.interrupt()
                                    mBitmapQueue.remove(bitmap)
                                    mRenderThread=null
                                }else{
                                    Log.e("print_logs", "RenderThread::run: Bitmap为空")
                                }
                            }
                        }
                    }
                } finally {
                    if (canvas != null) {
//                        mHolder.unlockCanvasAndPost(canvas)
                    }
                    Log.i("print_logs", "RenderThread::run: ..")
                }
            }
        }
    }

    fun clearQueue(){
        Log.i("print_logs", "BitmapRenderingSurfaceView::clearQueue: ")

        mBitmapQueue.forEach {
            it.recycle()
        }
        mBitmapQueue.clear()
//        mRenderThread!!.setRunning(false)
//        mRenderThread!!.interrupt()
    }
}