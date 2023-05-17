package zs.android.synthesis

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.FragmentTransaction
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import zs.android.synthesis.extend.assetsToBitmap
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

        supportFragmentManager.beginTransaction().apply {
            mFragments.forEach {
                add(R.id.fragment_container, it)
            }
            commit()
        }

        showFragment(mFragments[0])

        findViewById<AppCompatButton>(R.id.acBtn_img_labeler).setOnClickListener {
            showFragment(mFragments[0])
        }
        findViewById<AppCompatButton>(R.id.acBtn_cross_device).setOnClickListener {
            showFragment(mFragments[1])
        }
        findViewById<AppCompatButton>(R.id.acBtn_cronet).setOnClickListener {
            showFragment(mFragments[2])
        }
    }

    private fun showFragment(fragment: Fragment) {
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