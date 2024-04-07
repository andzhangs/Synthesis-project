package zs.android.synthesis

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import zs.android.synthesis.receiver.LocalReceiver


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val ACTION_ENCODE_1 = "zs.android.synthesis.encode.action1"
        val ACTION_ENCODE_2 = "zs.android.synthesis.encode.action2"
        val KEY = "key"
        val VALUE = "value"

        LocalReceiver.register(this, ACTION_ENCODE_1)
        LocalReceiver.register(this, ACTION_ENCODE_2)
        LocalReceiver.addListener(ACTION_ENCODE_1, object : LocalReceiver.LocalReceiverListener {
            override fun onCallback(intent: Intent) {
                Log.i(
                    "print_logs",
                    "onCallback-1: key= ${intent.getStringExtra(KEY)}, value= ${
                        intent.getStringExtra(VALUE)
                    }"
                )
            }

        })

        LocalReceiver.addListener(ACTION_ENCODE_2, object : LocalReceiver.LocalReceiverListener {
            override fun onCallback(intent: Intent) {
                Log.i(
                    "print_logs",
                    "onCallback-2: key= ${intent.getStringExtra(KEY)}, value= ${
                        intent.getStringExtra(VALUE)
                    }"
                )
            }
        })
    }

    companion object {
        init {
            System.loadLibrary("synthesis")
        }
    }
}