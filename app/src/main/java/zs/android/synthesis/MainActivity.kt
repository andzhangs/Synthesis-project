package zs.android.synthesis

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import zs.android.synthesis.fragment.CronetFragment
import zs.android.synthesis.fragment.CrossDeviceFragment
import zs.android.synthesis.fragment.ImageLabelerFragment
import zs.android.synthesis.receiver.LocalReceiver


class MainActivity : AppCompatActivity() {

    private val mFragments = arrayListOf(
        ImageLabelerFragment.newInstance(),
        CrossDeviceFragment.newInstance(),
        CronetFragment.newInstance()
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.i("print_logs", "输出json: ${stringFromJNI()}")

        supportFragmentManager.beginTransaction().apply {
            mFragments.forEach {
                add(R.id.fragment_container, it)
            }
            commit()
        }
        val ACTION_ENCODE_1 = "zs.android.synthesis.encode.action1"
        val ACTION_ENCODE_2 = "zs.android.synthesis.encode.action2"
        val KEY="key"
        val VALUE="value"

        LocalReceiver.register(this, ACTION_ENCODE_1)
        LocalReceiver.register(this, ACTION_ENCODE_2)
        LocalReceiver.get(ACTION_ENCODE_1)?.addListener(object :LocalReceiver.LocalReceiverListener{
            override fun onCallback(intent: Intent) {
                Log.i("print_logs", "MainActivity::onCallback-1: ${intent.getStringExtra(KEY)}")
            }

        })

        LocalReceiver.get(ACTION_ENCODE_2)?.addListener(object :LocalReceiver.LocalReceiverListener{
            override fun onCallback(intent: Intent) {
                Log.i("print_logs", "MainActivity::onCallback-2: ${intent.getStringExtra(KEY)}")
            }
        })


        showFragment(0)

        findViewById<AppCompatButton>(R.id.acBtn_img_labeler).setOnClickListener {
            showFragment(0)
            LocalReceiver.sendIntent(ACTION_ENCODE_1,Intent().apply {
                putExtra(KEY, "hello-1")
                putExtra(VALUE, "world-1")
            })
        }
        findViewById<AppCompatButton>(R.id.acBtn_cross_device).setOnClickListener {
            showFragment(1)
            LocalReceiver.sendIntent(ACTION_ENCODE_2,Intent().apply {
                putExtra(KEY, "hello-2")
                putExtra(VALUE, "world-2")
            })
        }
        findViewById<AppCompatButton>(R.id.acBtn_cronet).setOnClickListener {
            showFragment(2)
        }
    }

    private fun showFragment(position: Int) {
        val fragment = mFragments[position]
        supportFragmentManager.beginTransaction().apply {
            mFragments.forEach {
                if (it.javaClass.simpleName == fragment.javaClass.simpleName) {
                    show(fragment)
                } else {
                    hide(it)
                }
            }
            commit()
        }
    }

    external fun stringFromJNI(): String

    companion object {
        init {
            System.loadLibrary("synthesis")
        }
    }
}