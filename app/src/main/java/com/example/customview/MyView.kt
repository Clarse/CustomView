package com.example.customview

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.graphics.withRotation
import androidx.core.graphics.withTranslation
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import kotlinx.coroutines.*
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class MyView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr), LifecycleObserver {
    private var sinWaveSamplesPath = Path()
    private var rotatingJob: Job? = null
    private var mAngle = 10f
    private var mRadius = 0f
    private var mWidth = 0f
    private var mHeight = 0f
    private val fillCirclePaint = Paint().apply {
        style = Paint.Style.FILL
        color = ContextCompat.getColor(context, R.color.white)
    }
    private val solidLinePaint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 5f
        color = ContextCompat.getColor(context, R.color.white)
    }
    private val vectorLinePaint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 5f
        color = ContextCompat.getColor(context, R.color.teal_200)
    }
    private val textPaint = Paint().apply {
        textSize = 40f
        typeface = Typeface.DEFAULT_BOLD
        color = ContextCompat.getColor(context, R.color.white)
    }
    private val dashedLinePaint = Paint().apply {
        style = Paint.Style.STROKE
        pathEffect = DashPathEffect(floatArrayOf(10f, 10f), 0f)
        color = ContextCompat.getColor(context, R.color.yellow)
    }

    //view最终尺寸确定之后调用
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mWidth = w.toFloat()
        mHeight = h.toFloat()
        mRadius = if (w < h / 2) w / 2.toFloat() else h / 4.toFloat()
        mRadius -= 20f
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.apply {
            drawAxises(this)
            drawLabel(this)
            drawDashedCircle(this)
            drawVector(this)
            drawProjections(this)
            drawSinWave(this)
        }
    }

    //画基线
    private fun drawAxises(canvas: Canvas) {
        //将画布移动到新的位置然后绘制，然后恢复画布
        canvas.withTranslation(mWidth / 2, mHeight / 2) {
            drawLine(-mWidth / 2, 0f, mWidth / 2, 0f, solidLinePaint)
            drawLine(0f, -mHeight / 2, 0f, mHeight / 2, solidLinePaint)
        }
        canvas.withTranslation(mWidth / 2, mHeight / 4 * 3) {
            drawLine(-mWidth / 2, 0f, mWidth / 2, 0f, solidLinePaint)
        }
    }

    //画文字
    private fun drawLabel(canvas: Canvas) {
        canvas.apply {
            drawRect(100f, 100f, 500f, 200f, solidLinePaint)
            drawText("指数函数与旋转矢量", 120f, 170f, textPaint)
        }
    }

    //画虚线圆
    private fun drawDashedCircle(canvas: Canvas) {
        canvas.withTranslation(mWidth / 2, mHeight / 4 * 3) {
            drawCircle(0f, 0f, mRadius, dashedLinePaint)
        }
    }

    //画圆半径线
    private fun drawVector(canvas: Canvas) {
        canvas.withTranslation(mWidth / 2, mHeight / 4 * 3) {
            withRotation(-mAngle) {
                drawLine(0f, 0f, mRadius, 0f, vectorLinePaint)
            }
        }
    }

    //画两个点和两条垂直线
    private fun drawProjections(canvas: Canvas) {
        canvas.withTranslation(mWidth / 2, mHeight / 2) {
            drawCircle(mRadius * cos(mAngle.toRadians()), 0f, 10f, fillCirclePaint)
        }
        canvas.withTranslation(mWidth / 2, mHeight / 4 * 3) {
            drawCircle(mRadius * cos(mAngle.toRadians()), 0f, 10f, fillCirclePaint)
        }
        canvas.withTranslation(mWidth / 2, mHeight / 4 * 3) {
            val x = mRadius * cos(mAngle.toRadians())
            val y = mRadius * sin(mAngle.toRadians())
            withTranslation(x, -y) {
                drawLine(0f, 0f, 0f, y, solidLinePaint)
                drawLine(0f, 0f, 0f, -mHeight / 4 + y, dashedLinePaint)
            }
        }
    }

    //画正弦波浪线
    private fun drawSinWave(canvas: Canvas) {
        canvas.withTranslation(mWidth / 2, mHeight / 2) {
            val samplesCount = 50
            val dy = mHeight / 2 / samplesCount
            sinWaveSamplesPath.reset()
            sinWaveSamplesPath.moveTo(mRadius * cos(mAngle.toRadians()), 0f)
            repeat(samplesCount) {
                val x = mRadius * cos(it * -0.15 + mAngle.toRadians()).toFloat()
                val y = -dy * it
                sinWaveSamplesPath.quadTo(x, y, x, y)
            }
            drawPath(sinWaveSamplesPath, vectorLinePaint)
            drawTextOnPath("I love you", sinWaveSamplesPath, 1000f, -20f, textPaint)
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    private fun startRotating() {
        rotatingJob = CoroutineScope(Dispatchers.Main).launch {
            while (true) {
                delay(100)
                mAngle += 5f
                invalidate()
            }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    private fun pauseRotating() {
        rotatingJob?.cancel()
    }

    //扩展函数
    private fun Float.toRadians() = this / 180 * PI.toFloat()

}