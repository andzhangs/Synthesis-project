package com.module.other

import android.content.Context
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.os.BuildCompat
import androidx.databinding.DataBindingUtil
import com.module.other.databinding.ActivityMainBinding
import com.module.other.handlerthread.MyHandlerThread
import com.squareup.seismic.ShakeDetector

@BuildCompat.PrereleaseSdkCheck
class MainActivity : AppCompatActivity() {

    private lateinit var mDataBinding: ActivityMainBinding

    private var handlerThread: MyHandlerThread? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        onBackPressedDispatcher.addCallback(backPressedCallback)
        loadHandlerThread()
        loadSeismic()
        loadBackPressed()
    }

    private fun loadHandlerThread() {

        mDataBinding.acBtnHandlerThreadStart.setOnClickListener {
            handlerThread = MyHandlerThread("Thread_MainActivity").apply {
                start()

                sendMessage(Message.obtain().also {
                    it.arg1 = 200
                    it.arg2 = 2001
                    it.what = 20002
                    it.obj = "Hello，I'm from MainActivity."
                })
            }
        }

        mDataBinding.acBtnHandlerThreadQuit.setOnClickListener {
            if (BuildConfig.DEBUG) {
                Log.i("print_logs", "HandlerThread.isAlive-1: ${handlerThread?.isAlive}")
            }

            handlerThread?.quitSafely()
//            handlerThread.quit()
        }
    }


    /**
     * 谷歌手机晃动检测库
     */
    private fun loadSeismic() {
        mDataBinding.acBtnSeismic.setOnClickListener {
            val listener = ShakeDetector.Listener {
                Toast.makeText(this, "摇一摇", Toast.LENGTH_SHORT).show()
            }

            val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
            val shakeDetector = ShakeDetector(listener)
            shakeDetector.start(sensorManager)

//        val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
//        sensorManager.registerListener(object : SensorEventListener {
//            override fun onSensorChanged(event: SensorEvent?) {
//                if (BuildConfig.DEBUG) {
////                    Log.i("print_logs", "MainActivity::onSensorChanged: ${event?.accuracy}")
//                }
//            }
//
//            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
//                if (BuildConfig.DEBUG) {
////                    Log.i("print_logs", "MainActivity::onAccuracyChanged: $accuracy")
//                }
//            }
//
//        }, sensor, SensorManager.SENSOR_DELAY_FASTEST)
        }
    }


    private fun loadBackPressed() {
        mDataBinding.acBtnBackPressed.setOnClickListener {
            if (BuildConfig.DEBUG) {
                Log.i(
                    "print_logs",
                    "handleOnBackPressed: ${backPressedCallback.handleOnBackPressed()}"
                )
            }
            backPressedCallback.remove()
        }
    }

    private val backPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (BuildConfig.DEBUG) {
                Log.i("print_logs", "MainActivity::handleOnBackPressed: ")
            }
        }
    }

    override fun onPause() {
        super.onPause()
        if (BuildConfig.DEBUG) {
            Log.i("print_logs", "HandlerThread.isAlive-2: ${handlerThread?.isAlive}")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (BuildConfig.DEBUG) {
            Log.i("print_logs", "MainActivity::onDestroy: ")
        }
    }
}