package zs.module.material.ui.fragment

import android.os.Bundle
import android.view.View
import android.view.ViewAnimationUtils
import com.google.android.material.circularreveal.CircularRevealFrameLayout
import com.google.android.material.circularreveal.CircularRevealGridLayout
import com.google.android.material.circularreveal.CircularRevealLinearLayout
import com.google.android.material.circularreveal.CircularRevealRelativeLayout
import com.google.android.material.circularreveal.cardview.CircularRevealCardView
import com.google.android.material.circularreveal.coordinatorlayout.CircularRevealCoordinatorLayout
import zs.module.material.R
import zs.module.material.base.BaseMaterialFragment


class CircularRevealFragment private constructor(mIndexName: String) :
    BaseMaterialFragment(mIndexName) {
    override fun setLayoutRes() = R.layout.fragment_circular_reveal

    companion object {

        @JvmStatic
        fun newInstance() = CircularRevealFragment("CircularReveal")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val cardView = mRootView.findViewById<CircularRevealCardView>(R.id.card_view)
        cardView.setOnClickListener {
            val centerX = cardView.width / 2
            val centerY = cardView.height / 2
            val startRadius = 0f
            val endRadius = (cardView.width.coerceAtLeast(cardView.height) / 2).toFloat()

            val circularReveal=ViewAnimationUtils.createCircularReveal(cardView,centerX,centerY,startRadius,endRadius)
            circularReveal.duration = 1000
            circularReveal.start()
        }

        val frameLayout = mRootView.findViewById<CircularRevealFrameLayout>(R.id.card_view_2)
        frameLayout.setOnClickListener {
            val centerX = frameLayout.width / 2
            val centerY = frameLayout.height / 2
            val startRadius = 0f
            val endRadius = (frameLayout.width.coerceAtLeast(frameLayout.height) / 2).toFloat()

            val circularReveal=ViewAnimationUtils.createCircularReveal(frameLayout,centerX,centerY,startRadius,endRadius)
            circularReveal.duration = 1000
            circularReveal.start()
        }

        val linearLayout = mRootView.findViewById<CircularRevealLinearLayout>(R.id.card_view_3)
        linearLayout.setOnClickListener {
            val centerX = linearLayout.width / 2
            val centerY = linearLayout.height / 2
            val startRadius = 0f
            val endRadius = (linearLayout.width.coerceAtLeast(linearLayout.height) / 2).toFloat()

            val circularReveal=ViewAnimationUtils.createCircularReveal(linearLayout,centerX,centerY,startRadius,endRadius)
            circularReveal.duration = 1000
            circularReveal.start()
        }

        val gridLayout = mRootView.findViewById<CircularRevealGridLayout>(R.id.card_view_4)
        gridLayout.setOnClickListener {
            val centerX = gridLayout.width / 2
            val centerY = gridLayout.height / 2
            val startRadius = 0f
            val endRadius = (gridLayout.width.coerceAtLeast(gridLayout.height) / 2).toFloat()

            val circularReveal=ViewAnimationUtils.createCircularReveal(gridLayout,centerX,centerY,startRadius,endRadius)
            circularReveal.duration = 1000
            circularReveal.start()
        }

        val relativeLayout = mRootView.findViewById<CircularRevealRelativeLayout>(R.id.card_view_5)
        relativeLayout.setOnClickListener {
            val centerX = relativeLayout.width / 2
            val centerY = relativeLayout.height / 2
            val startRadius = 0f
            val endRadius = (relativeLayout.width.coerceAtLeast(relativeLayout.height) / 2).toFloat()

            val circularReveal=ViewAnimationUtils.createCircularReveal(relativeLayout,centerX,centerY,startRadius,endRadius)
            circularReveal.duration = 1000
            circularReveal.start()
        }

        val coordinatorLayout = mRootView.findViewById<CircularRevealCoordinatorLayout>(R.id.card_view_6)
        coordinatorLayout.setOnClickListener {
            val centerX = coordinatorLayout.width / 2
            val centerY = coordinatorLayout.height / 2
            val startRadius = 0f
            val endRadius = (coordinatorLayout.width.coerceAtLeast(coordinatorLayout.height) / 2).toFloat()

            val circularReveal=ViewAnimationUtils.createCircularReveal(coordinatorLayout,centerX,centerY,startRadius,endRadius)
            circularReveal.duration = 1000
            circularReveal.start()
        }
    }
}