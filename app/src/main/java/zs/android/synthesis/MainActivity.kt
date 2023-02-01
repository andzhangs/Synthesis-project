package zs.android.synthesis

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import zs.android.synthesis.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    /**
     * A native method that is implemented by the 'synthesis' native library,
     * which is packaged with this application.
     */
    external fun stringFromJNI(): String

    companion object {
        // Used to load the 'synthesis' library on application startup.
        init {
            System.loadLibrary("synthesis")
        }
    }
}