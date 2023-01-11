package zs.android.synthesis

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import zs.android.synthesis.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Example of a call to a native method
        binding.sampleText.text = stringFromJNI()
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