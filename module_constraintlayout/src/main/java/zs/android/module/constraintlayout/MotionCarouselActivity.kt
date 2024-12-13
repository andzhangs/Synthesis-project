package zs.android.module.constraintlayout

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.helper.widget.Carousel
import com.google.android.material.card.MaterialCardView
import zs.android.module.constraintlayout.databinding.ActivityMotionCarouselBinding

class MotionCarouselActivity : AppCompatActivity() {

    private lateinit var mDataBinding: ActivityMotionCarouselBinding

    var colors = intArrayOf(
        Color.parseColor("#ffd54f"),
        Color.parseColor("#ffca28"),
        Color.parseColor("#ffc107"),
        Color.parseColor("#ffb300"),
        Color.parseColor("#ffa000"),
        Color.parseColor("#ff8f00"),
        Color.parseColor("#ff6f00"),
        Color.parseColor("#c43e00")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDataBinding = ActivityMotionCarouselBinding.inflate(layoutInflater).apply {
            setContentView(this.root)
        }
        setupCarousel()
    }

    private fun setupCarousel() {
        val carousel = findViewById<Carousel>(R.id.carousel) ?: return
        val numImages = colors.size

        carousel.setAdapter(object : Carousel.Adapter {
            override fun count(): Int {
                return numImages
            }

            override fun populate(view: View, index: Int) {
                if (view is MaterialCardView) {
                    view.setBackgroundColor(colors[index])
                }
            }

            override fun onNewItem(index: Int) {
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        mDataBinding.unbind()
    }
}