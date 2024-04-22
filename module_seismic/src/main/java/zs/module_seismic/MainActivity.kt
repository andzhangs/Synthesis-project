package zs.module_seismic

import android.content.Context
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import com.squareup.seismic.ShakeDetector

/**
 * 手机晃动检测库
 */
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loadSeismic()
    }

    /**
     * 谷歌手机晃动检测库
     */
    private fun loadSeismic() {
        findViewById<AppCompatButton>(R.id.acBtn_seismic).setOnClickListener {
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
}