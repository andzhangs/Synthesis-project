package com.module.other

import android.content.Context
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import com.module.other.databinding.ActivityMainBinding
import com.module.other.handlerthread.MyHandlerThread
import com.squareup.seismic.ShakeDetector

class MainActivity : AppCompatActivity() {

    private lateinit var mDataBinding: ActivityMainBinding

    private var handlerThread: MyHandlerThread? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        loadHandlerThread()
        loadSeismic()
        loadBackPressed()
    }

    private fun loadHandlerThread() {

        mDataBinding.acBtnHandlerThreadStart.setOnClickListener {
            onBackPressedDispatcher.addCallback(backPressedCallback)
            handlerThread = MyHandlerThread("Thread_MainActivity").apply {
                start()

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
            Log.i(
                "print_logs",
                "handleOnBackPressed: ${backPressedCallback.handleOnBackPressed()}"
            )
            backPressedCallback.remove()
        }
    }

    private val backPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            Log.i("print_logs", "MainActivity::handleOnBackPressed: ")
        }
    }

    override fun onResume() {
        super.onResume()
        Log.i("print_logs", "MainActivity::onResume: ${getString()}")
    }

    override fun onPause() {
        super.onPause()
        Log.i("print_logs", "HandlerThread.isAlive-2: ${handlerThread?.isAlive}")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i("print_logs", "MainActivity::onDestroy: ")
    }

    external fun getString(): String
}