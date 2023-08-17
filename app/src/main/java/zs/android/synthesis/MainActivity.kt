package zs.android.synthesis

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import zs.android.synthesis.fragment.CronetFragment
import zs.android.synthesis.fragment.CrossDeviceFragment
import zs.android.synthesis.fragment.ImageLabelerFragment


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

        showFragment(0)

        findViewById<AppCompatButton>(R.id.acBtn_img_labeler).setOnClickListener {
            showFragment(0)
        }
        findViewById<AppCompatButton>(R.id.acBtn_cross_device).setOnClickListener {
            showFragment(1)
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