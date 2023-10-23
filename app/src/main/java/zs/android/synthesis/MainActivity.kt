package zs.android.synthesis

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import zs.android.synthesis.able.ResponseCloseable
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

        val responseCloseable = ResponseCloseable()
        responseCloseable.name = "Hello ROwld!"
        responseCloseable.age = 18
        responseCloseable.toString()
        try {
            responseCloseable.use {
                it.toString()
            }
        }catch (e:Exception){
            e.printStackTrace()
        }

        Log.i("print_logs", "输出json: ${stringFromJNI()}")

        supportFragmentManager.beginTransaction().apply {
            mFragments.forEach {
                add(R.id.fragment_container, it)
            }
            commit()
        }
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


        showFragment(0)

        findViewById<AppCompatButton>(R.id.acBtn_img_labeler).setOnClickListener {
            showFragment(0)
            LocalReceiver.sendIntent(ACTION_ENCODE_1, Intent().apply {
                putExtra(KEY, "hello-1")
                putExtra(VALUE, "world-1")
            })
        }
        findViewById<AppCompatButton>(R.id.acBtn_cross_device).setOnClickListener {
            showFragment(1)
            LocalReceiver.sendIntent(ACTION_ENCODE_2, Intent().apply {
                putExtra(KEY, "hello-2")
                putExtra(VALUE, "world-2")
            })
        }
        findViewById<AppCompatButton>(R.id.acBtn_cronet).setOnClickListener {
            showFragment(2)
        }



        val activityLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == RESULT_OK) {
                    if (BuildConfig.DEBUG) {
                        Log.i("print_logs", "MainActivity::onCreate: ${it.data?.getStringExtra(Viewpager2Activity.RESULT_VALUE)}")
                    }
                }
            }

        findViewById<AppCompatButton>(R.id.acBtn_viewpager2).setOnClickListener {
            activityLauncher.launch(Intent(this, Viewpager2Activity::class.java))
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