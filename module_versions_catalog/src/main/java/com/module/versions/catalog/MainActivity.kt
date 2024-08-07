package com.module.versions.catalog

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.MotionEvent
import android.widget.Magnifier
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {


    private lateinit var mTextView: TextView
    private lateinit var magnifier: Magnifier

    @SuppressLint("MissingInflatedId", "ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Toast.makeText(this, "提示：${BuildConfig.BASE_HTTP_URL}", Toast.LENGTH_SHORT).show()

        mTextView = findViewById(R.id.text_view)


        // 放大文本
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            magnifier = Magnifier.Builder(mTextView).build()
            mTextView.setOnTouchListener { v, event ->
                when (event.actionMasked) {
                    MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                        val viewPosition=IntArray(2)
                        v.getLocationOnScreen(viewPosition)
                        magnifier.show(event.rawX-viewPosition[0],event.rawY-viewPosition[1])
                    }

                    MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                        magnifier.dismiss()
                    }

                    else -> {}
                }
                true
            }
        }
    }
}