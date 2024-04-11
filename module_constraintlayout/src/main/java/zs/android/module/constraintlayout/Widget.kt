package zs.android.module.constraintlayout

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Region
import android.text.TextPaint
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.View
import androidx.annotation.ColorInt
import androidx.core.graphics.BlendModeCompat
import androidx.core.graphics.PaintCompat


/**
 *
 * @author zhangshuai
 * @date 2024/4/11 13:45
 * @description 自定义类描述
 */
class WordParticleView @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    View(context, attrs, defStyleAttr) {
    private var text: CharArray? = "永远的女神".toCharArray()
    private var mDM: DisplayMetrics? = null
    var fm = Paint.FontMetrics()
    var shouldUpdateTextPath = true
    private var mPaint: TextPaint? = null

    init {
        mDM = resources.displayMetrics
        initPaint()
    }

    private fun initPaint() {
        //否则提供给外部纹理绘制
        mPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)
        mPaint!!.isAntiAlias = false
        mPaint!!.strokeCap = Paint.Cap.ROUND
        mPaint!!.textSize = dp2px(150f)
        mPaint!!.color = Color.BLACK
        mPaint!!.style = Paint.Style.FILL_AND_STROKE
        PaintCompat.setBlendMode(mPaint!!, BlendModeCompat.LIGHTEN)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        var widthSize = MeasureSpec.getSize(widthMeasureSpec)
        if (widthMode != MeasureSpec.EXACTLY) {
            widthSize = mDM!!.widthPixels
        }
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        var heightSize = MeasureSpec.getSize(heightMeasureSpec)
        if (heightMode != MeasureSpec.EXACTLY) {
            heightSize = widthSize
        }
        setMeasuredDimension(widthSize, heightSize)
    }

    fun setText(text: String?) {
        if (text == null) return
        shouldUpdateTextPath = true
        this.text = text.toCharArray()
    }

    //记录点位，保存重合点位
    var points: MutableList<Particle> = ArrayList()
    var textPath: Path = Path() // 文本路径

    //文本区域，这里是指文字路径区域，并不是文字大小区域
    var textPathRegion = Region()

    //这里才是文本区域
    var mainRegion = Region()

    //文本区域边界
    var textRect = RectF()

    //分片区域边界
    var measureRect = Rect()
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        shouldUpdateTextPath = true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (text == null) return
        val saveRecord = canvas.save()
        canvas.translate(width / 2f, height / 2f)
        val measureTextWidth = mPaint!!.measureText(text, 0, text!!.size)
        val baseline = getTextPaintBaseline(mPaint)
        val step = 3 //步长
        if (shouldUpdateTextPath) {
            textPath.reset()
            mPaint!!.getTextPath(text, 0, text!!.size, -(measureTextWidth / 2f), baseline, textPath)
            shouldUpdateTextPath = false

            //染色区域设置
            textPath.computeBounds(textRect, true)
            mainRegion[textRect.left.toInt(), textRect.top.toInt(), textRect.right.toInt()] =
                textRect.bottom.toInt()
            textPathRegion.setPath(textPath, mainRegion)
            val textRectWidth = textRect.width()
            val textRectHeight = textRect.height()
            var index = 0
            var i = 0
            while (i < textRectHeight) {
                var j = 0
                while (j < textRectWidth) {
                    val row = (-textRectHeight / 2 + i * step).toInt()
                    val col = (-textRectWidth / 2 + j * step).toInt()
                    measureRect[col, row, col + step] = row + step
                    if (!textPathRegion.contains(measureRect.centerX(), measureRect.centerY())) {
                        j += step
                        continue
                    }
                    var p = if (points.size > index) points[index] else null
                    index++
                    if (p == null) {
                        p = Particle()
                        points.add(p)
                    }
                    p.x = measureRect.centerX().toFloat()
                    p.y = measureRect.centerY().toFloat()
                    val random = Math.random()
                    hsl[0] = (random * 360).toFloat()
                    hsl[1] = 0.5f
                    hsl[2] = 0.5f //最亮
                    p.color = HSLToColor(hsl)
                    val randomInt = 1 + (Math.random() * 100).toInt()
                    val t = ((if (randomInt % 2 == 0) -1f else 1f) * Math.random()).toFloat()
                    p.radius = (step * random).toFloat()
                    p.range = (step * 5).toFloat()
                    p.t = t
                    j += step
                }
                i += step
            }
            while (points.size > index + 1) {
                points.removeAt(points.size - 1)
            }
        }
        val textSize = mPaint!!.textSize
        var i = 0
        while (i < points.size) {
            val point = points[i]
            mPaint!!.color = point.color
            canvas.drawCircle(point.x, point.y, point.radius, mPaint!!)
            point.update()
            i += 2
        }
        mPaint!!.textSize = textSize
        canvas.restoreToCount(saveRecord)
        postInvalidateDelayed(0)
    }

    fun getTextPaintBaseline(p: Paint?): Float {
        p!!.getFontMetrics(fm)
        val fontMetrics = fm
        return (fontMetrics.descent - fontMetrics.ascent) / 2 - fontMetrics.descent
    }

    fun dp2px(dp: Float): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics)
    }

    var hsl = FloatArray(3)

    class Particle : PointF() {
        var t = 0f
        var color = 0
        var radius = 0f
        var range = 0f
        fun update() {
            radius = radius + t
            if (radius >= range || radius <= 0) {
                t = -t
            }
            if (radius < 0) {
                radius = 0f
            }
            if (radius > range) {
                radius = range
            }
        }
    }

    companion object {
        fun argb(alpha: Float, red: Float, green: Float, blue: Float): Int {
            return (alpha * 255.0f + 0.5f).toInt() shl 24 or
                    ((red * 255.0f + 0.5f).toInt() shl 16) or
                    ((green * 255.0f + 0.5f).toInt() shl 8) or (blue * 255.0f + 0.5f).toInt()
        }

        @ColorInt
        fun HSLToColor(hsl: FloatArray): Int {
            val h = hsl[0]
            val s = hsl[1]
            val l = hsl[2]
            val c = (1f - Math.abs(2 * l - 1f)) * s
            val m = l - 0.5f * c
            val x = c * (1f - Math.abs(h / 60f % 2f - 1f))
            val hueSegment = h.toInt() / 60
            var r = 0
            var g = 0
            var b = 0
            when (hueSegment) {
                0 -> {
                    r = Math.round(255 * (c + m))
                    g = Math.round(255 * (x + m))
                    b = Math.round(255 * m)
                }

                1 -> {
                    r = Math.round(255 * (x + m))
                    g = Math.round(255 * (c + m))
                    b = Math.round(255 * m)
                }

                2 -> {
                    r = Math.round(255 * m)
                    g = Math.round(255 * (c + m))
                    b = Math.round(255 * (x + m))
                }

                3 -> {
                    r = Math.round(255 * m)
                    g = Math.round(255 * (x + m))
                    b = Math.round(255 * (c + m))
                }

                4 -> {
                    r = Math.round(255 * (x + m))
                    g = Math.round(255 * m)
                    b = Math.round(255 * (c + m))
                }

                5, 6 -> {
                    r = Math.round(255 * (c + m))
                    g = Math.round(255 * m)
                    b = Math.round(255 * (x + m))
                }
            }
            r = constrain(r, 0, 255)
            g = constrain(g, 0, 255)
            b = constrain(b, 0, 255)
            return Color.rgb(r, g, b)
        }

        private fun constrain(amount: Int, low: Int, high: Int): Int {
            return if (amount < low) low else Math.min(amount, high)
        }
    }
}
