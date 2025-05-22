package zs.android.module.thread

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import zs.android.module.thread.databinding.ActivityMainBinding
import zs.android.module.thread.thread.MyHandlerThread
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {


    private lateinit var mDataBinding: ActivityMainBinding
    private var handlerThread: MyHandlerThread? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        loadHandlerThread()
    }

    @SuppressLint("NewApi")
    private fun loadHandlerThread() {

        mDataBinding.acBtnHandlerThreadStart.setOnClickListener {

            if (handlerThread == null) {
                handlerThread = MyHandlerThread("Thread_MainActivity").apply {
//                    NonCancellable.start()

                    start()
                }
            }

            lifecycleScope.launch {
                repeat(10){times->
                    handlerThread!!.sendMessage(Message.obtain().also {

                        if (times % 2 == 0 ) {
                            it.isAsynchronous = true
                        }

                        it.arg1 = 200
                        it.arg2 = 2001
                        it.what = 20002
                        it.obj = "Hello，I'm from MainActivity. $times "
                    })

                    delay(500)
                }
            }
        }

        mDataBinding.acBtnHandlerThreadQuit.setOnClickListener {

            Log.i("print_logs", "HandlerThread.isAlive-1: ${handlerThread?.isAlive}")

//            val getThreadHandler = handlerThread?.javaClass?.getMethod("getThreadHandler")
//            getThreadHandler?.isAccessible = true
//            val handler= getThreadHandler?.invoke(handlerThread,null) as Handler


            handlerThread?.quitSafely()
//            handlerThread.quit()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mDataBinding.unbind()
    }
}