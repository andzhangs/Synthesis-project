package zs.android.module.animation

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.drawToBitmap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<AppCompatTextView>(R.id.tv_info).setOnClickListener {
            AnimatorSet().apply {
                ObjectAnimator.ofFloat(it, "TranslationY", 200F).apply {
                    duration = 1000
                    start()
                    play(this)
                }

                ObjectAnimator.ofFloat(it, "alpha", 1F, 0F).apply {
                    duration = 1000
                    start()
                    play(this)
                }
                start()
            }
        }

        findViewById<AppCompatImageButton>(R.id.acImgBtn_camera).setOnClickListener {
            //方式一
//            val animX = ObjectAnimator.ofFloat(it, "x", 50F)
//            val animY = ObjectAnimator.ofFloat(it, "y", 100F)
//            AnimatorSet().apply {
//                playTogether(animX, animY)
//                start()
//            }

//            //方式二
//            val pvhX=PropertyValuesHolder.ofFloat("x",50F)
//            val pvhY=PropertyValuesHolder.ofFloat("y",100F)
//            ObjectAnimator.ofPropertyValuesHolder(it,pvhX,pvhY).start()
//
//            //方式三
            it.animate().x(50F).y(100F).start()
//
//            //方式四
//            (AnimatorInflater.loadAnimator(this,R.animator.property_animator) as AnimatorSet).apply {
//                setTarget(it)
//                start()
//            }
//
//            //方式五
//            (AnimatorInflater.loadAnimator(this,R.animator.animator_value) as ValueAnimator).apply {
//                 addUpdateListener { valueAnimator->
//                     it.translationX= valueAnimator.animatedValue as Float
//                 }
//                start()
//            }
        }

        val rootView = findViewById<ConstraintLayout>(R.id.cl_root)
        val drawBitmapView = findViewById<AppCompatImageView>(R.id.acIv_draw_bitmap)

        val animListView = findViewById<AppCompatImageView>(R.id.acImg_animList)
        animListView.apply {

            setOnClickListener {
//                setBackgroundResource(R.drawable.rocket_thrust)
//                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
////                    (this.background as AnimatedImageDrawable).start()
//                }

                CoroutineScope(Dispatchers.Main).launch {
                    repeat(10) {
                        drawBitmapView.setImageBitmap(rootView.drawToBitmap())
                        delay(300L)

                        if (this.isActive) {
                            if (BuildConfig.DEBUG) {
                                Log.i("print_logs", "onCreate: $it")
                            }
                        }
//
                        if (it == 5) {
//                            this.cancel()
                        }
                        if (!this.isActive) {
                            if (BuildConfig.DEBUG) {
                                Log.e("print_logs", "onCreate: 停止了！")
                            }
                        }
                    }
                }

            }
            setOnLongClickListener {
                drawBitmapView.setImageBitmap(rootView.drawToBitmap())  // view转bitmap
                true
            }
        }

        findViewById<AppCompatButton>(R.id.acBtn_anim).setOnClickListener {
            startActivity(Intent(this, AnimShowAndHideViewActivity::class.java))
        }
    }
}