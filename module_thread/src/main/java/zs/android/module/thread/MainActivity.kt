package zs.android.module.thread

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import kotlinx.coroutines.NonCancellable
import zs.android.module.thread.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {


    private lateinit var mDataBinding: ActivityMainBinding
    private var handlerThread: zs.android.module.thread.thread.MyHandlerThread? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDataBinding=DataBindingUtil.setContentView(this,R.layout.activity_main)
        loadHandlerThread()
    }

    private fun loadHandlerThread() {

        mDataBinding.acBtnHandlerThreadStart.setOnClickListener {
            handlerThread =
                zs.android.module.thread.thread.MyHandlerThread("Thread_MainActivity").apply {
                    NonCancellable.start()

//                sendMessage(Message.obtain().also {
//                    it.arg1 = 200
//                    it.arg2 = 2001
//                    it.what = 20002
//                    it.obj = "Hello，I'm from MainActivity."
//                })
                }
        }

        mDataBinding.acBtnHandlerThreadQuit.setOnClickListener {

            Log.i("print_logs", "HandlerThread.isAlive-1: ${handlerThread?.isAlive}")

            handlerThread?.quitSafely()
//            handlerThread.quit()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mDataBinding.unbind()
    }
}