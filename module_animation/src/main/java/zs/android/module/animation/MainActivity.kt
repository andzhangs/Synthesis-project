package zs.android.module.animation

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.content.Intent
import android.graphics.drawable.AnimatedImageDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.AnimationSet
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.animation.addPauseListener

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<AppCompatTextView>(R.id.tv_info).setOnClickListener {
            AnimatorSet().apply {
                ObjectAnimator.ofFloat(it, "TranslationY", 100F).apply {
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
            val animX = ObjectAnimator.ofFloat(it, "x", 50F)
            val animY = ObjectAnimator.ofFloat(it, "y", 100F)
            AnimatorSet().apply {
                playTogether(animX, animY)
                start()
            }

//            //方式二
//            val pvhX=PropertyValuesHolder.ofFloat("x",50F)
//            val pvhY=PropertyValuesHolder.ofFloat("y",100F)
//            ObjectAnimator.ofPropertyValuesHolder(it,pvhX,pvhY).start()
//
//            //方式三
//            it.animate().x(50F).y(100F).start()
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

        findViewById<AppCompatImageView>(R.id.acImg_animList).apply {
            setBackgroundResource(R.drawable.rocket_thrust)
            setOnClickListener {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
//                    (this.background as AnimatedImageDrawable).start()
                }
            }
        }

        findViewById<AppCompatButton>(R.id.acBtn_anim).setOnClickListener {
            startActivity(Intent(this, AnimShowAndHideViewActivity::class.java))
        }
    }
}