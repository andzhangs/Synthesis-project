package zs.android.module.widget.widget.flip

import android.content.Context
import android.graphics.Camera
import android.util.AttributeSet
import android.view.View
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.DecelerateInterpolator
import android.view.animation.Transformation
import android.widget.FrameLayout
import kotlin.math.abs

/**
 *
 * @author zhangshuai
 * @date 2023/11/27 16:28
 * @mark 翻转
 */
class FlipGroupLayout @JvmOverloads constructor(
    private val context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes), Animation.AnimationListener,
    OnSwipeListener, View.OnClickListener {

    interface OnFlipListener {

        fun onFlipStart(parent: FlipGroupLayout)

        fun onFlipEnd(parent: FlipGroupLayout)
    }

    private enum class Direction {
        UP,
        DOWN,
        LEFT,
        RIGHT
    }

    companion object {
        private const val ANIM_DURATION_MILLIS = 500L
    }

    private var mOnFlipListener: OnFlipListener? = null
    private var flipAnimator: FlipAnimator
    private var isFlipped = false
    private var direction: Direction = Direction.DOWN
    private var mOnSwipeTouchListener: OnSwipeTouchListener? = null
    private var frontView: View? = null
    private var backView: View? = null

    init {
        flipAnimator = FlipAnimator().apply {
            setAnimationListener(this@FlipGroupLayout)
            interpolator = DecelerateInterpolator()
            duration = ANIM_DURATION_MILLIS
        }
        direction = Direction.DOWN
        isSoundEffectsEnabled = true
        mOnSwipeTouchListener = OnSwipeTouchListener(context)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        if (childCount > 2) {
            return
        }

        getChildAt(0).apply {
            frontView = this
            setOnTouchListener(mOnSwipeTouchListener)
            setOnClickListener(this@FlipGroupLayout)
        }

        getChildAt(1).apply {
            backView = this
            setOnTouchListener(mOnSwipeTouchListener)
            setOnClickListener(this@FlipGroupLayout)
        }
        mOnSwipeTouchListener?.addSwipeListener(this)
        reset()
    }

    private fun reset() {
        isFlipped = false
        direction = Direction.DOWN
        frontView?.visibility = View.VISIBLE
        backView?.visibility = View.GONE
    }

    fun toggleUp() {
        direction = Direction.UP
        startAnimation()
    }

    fun toggleDown() {
        direction = Direction.DOWN
        startAnimation()
    }

    fun toggleLeft() {
        direction = Direction.LEFT
        startAnimation()
    }

    fun toggleRight() {
        direction = Direction.RIGHT
        startAnimation()
    }

    private fun startAnimation() {
        flipAnimator.setVisibilitySwapped()
        startAnimation(flipAnimator)
    }

    //----------------------------------------------------------------------------------------------
    override fun onSwipeLeft() {
        toggleLeft()
    }

    override fun onSwipeRight() {
        toggleRight()
    }

    override fun onSwipeUp() {
        toggleUp()
    }

    override fun onSwipeDown() {
        toggleDown()
    }

    //----------------------------------------------------------------------------------------------

    override fun onAnimationStart(animation: Animation?) {
        mOnFlipListener?.onFlipStart(this)
    }

    override fun onAnimationEnd(animation: Animation?) {
        mOnFlipListener?.onFlipEnd(this)

        direction = when (direction) {
            Direction.UP -> Direction.DOWN
            Direction.DOWN -> Direction.UP
            Direction.LEFT -> Direction.RIGHT
            Direction.RIGHT -> Direction.LEFT
        }
    }

    override fun onAnimationRepeat(animation: Animation?) {

    }


    //----------------------------------------------------------------------------------------------

    override fun onClick(v: View?) {

    }

    //----------------------------------------------------------------------------------------------
    fun setOnFlipListener(listener: OnFlipListener?) {
        mOnFlipListener = listener
    }

    fun setAnimationListener(listener: AnimationListener?) {
        flipAnimator.setAnimationListener(listener)
    }

    //----------------------------------------------------------------------------------------------

    private inner class FlipAnimator : Animation() {

        private val EXPERIMENTAL_VALUE = 50.0f

        private lateinit var mCamera: Camera
        private var centerX = 0f
        private var centerY = 0f
        private var visibilitySwapped = false

        init {
            fillAfter = true
        }

        override fun initialize(width: Int, height: Int, parentWidth: Int, parentHeight: Int) {
            super.initialize(width, height, parentWidth, parentHeight)
            mCamera = Camera()
            centerX = (width / 2).toFloat()
            centerY = (height / 2).toFloat()
        }

        override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
            val radians = Math.PI * interpolatedTime

            var degrees = (180.0 * radians / Math.PI).toFloat()

            if (direction == Direction.UP || direction == Direction.LEFT) {
                degrees = -degrees
            }

            if (interpolatedTime >= 0.5f) {
                when (direction) {
                    Direction.UP, Direction.LEFT -> {
                        degrees += 180.0f
                    }

                    Direction.DOWN, Direction.RIGHT -> {
                        degrees -= 180.0f
                    }
                }

                if (!visibilitySwapped) {
                    toggleView()
                    visibilitySwapped = true
                }
            }

            val matrix = t?.matrix
            mCamera.save()
            mCamera.translate(0.0f, 0.0f, (EXPERIMENTAL_VALUE * abs(radians)).toFloat())
            when (direction) {
                Direction.UP, Direction.DOWN -> {
                    mCamera.rotateX(degrees)
                    mCamera.rotateY(0f)
                }

                Direction.LEFT, Direction.RIGHT -> {
                    mCamera.rotateX(0f)
                    mCamera.rotateY(degrees)
                }
            }
            mCamera.rotateZ(0f)
            mCamera.getMatrix(matrix)
            mCamera.restore()

            matrix?.preTranslate(-centerX, -centerY)
            matrix?.postTranslate(centerX, centerY)
        }

        private fun toggleView() {
            if (frontView == null || backView == null) {
                return
            }

            if (isFlipped) {
                frontView?.visibility = View.VISIBLE
                backView?.visibility = View.GONE
            } else {
                frontView?.visibility = View.GONE
                backView?.visibility = View.VISIBLE
            }
            isFlipped = !isFlipped
        }

        fun setVisibilitySwapped() {
            visibilitySwapped = false
        }
    }
}