package zs.android.module.animation

import android.animation.Animator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.graphics.alpha
import androidx.core.view.ViewCompat.animate
import androidx.core.widget.ContentLoadingProgressBar
import androidx.core.widget.NestedScrollView

class AnimShowAndHideViewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_anim_show_and_hide_view2)

        val scrollView=findViewById<NestedScrollView>(R.id.nScrollView)

        scrollView.visibility=View.GONE
        val duration=resources.getInteger(android.R.integer.config_shortAnimTime).toLong()

        scrollView.apply {
            alpha = 0F
            visibility = View.VISIBLE
            animate().alpha(1F).setDuration(duration).setListener(null)
        }

        val progressBar=findViewById<ContentLoadingProgressBar>(R.id.clProgressBar)
        progressBar.apply {
            animate().alpha(1F).setDuration(duration).setListener(object :Animator.AnimatorListener{
                override fun onAnimationStart(animation: Animator?) {

                }

                override fun onAnimationEnd(animation: Animator?) {
                    progressBar.visibility=View.GONE
                }

                override fun onAnimationCancel(animation: Animator?) {

                }

                override fun onAnimationRepeat(animation: Animator?) {

                }

            })
        }


    }
}