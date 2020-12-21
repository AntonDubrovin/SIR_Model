package com.example.sir_model

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

@SuppressLint("CustomViewStyleable")
class BoardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0,
) : View(context, attrs, defStyleAttr, defStyleRes) {

    private val paintBlack = Paint().apply {
        color = Color.GRAY
        isAntiAlias = true
        strokeWidth = 10F
        style = Paint.Style.FILL
    }

    private val paintGreen = Paint().apply {
        color = Color.parseColor("#00FA9A")
        isAntiAlias = true
        strokeWidth = 10F
        style = Paint.Style.FILL
    }

    private val paintRed = Paint().apply {
        color = Color.parseColor("#DC143C")
        isAntiAlias = true
        strokeWidth = 10F
        style = Paint.Style.FILL
    }

    private val paintBlue = Paint().apply {
        color = Color.parseColor("#00BFFF")
        isAntiAlias = true
        strokeWidth = 10F
        style = Paint.Style.FILL
    }

    private var startTime: Long = 0
    private var currentTime: Long = 0
    private var sz = 0f

    init {

        val a: TypedArray = context.obtainStyledAttributes(
            attrs, R.styleable.MyView, defStyleAttr, defStyleRes
        )
        startTime = System.currentTimeMillis()
        currentTime = startTime
        try {
        } finally {
            a.recycle()
        }

    }

    private fun drawCell(row: Int, column: Int, sz: Float, canvas: Canvas, paint: Paint) {
        canvas.drawRoundRect(
            (row - 1) * sz,
            (column - 1) * sz,
            row * sz,
            column * sz,
            sz / 4,
            sz / 4,
            paint
        )
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.save()
        currentTime = System.currentTimeMillis()
        sz = width / MainActivity.instance.dim.toFloat()
        if (MainActivity.instance.drr >= 0) {
            for (row in 1..MainActivity.instance.dim) {
                for (column in 1..MainActivity.instance.dim) {
                    drawCell(row, column, sz, canvas, paintBlack)
                }
            }

            for (i in MainActivity.instance.list) {
                when {
                    i.third == 0 -> {
                        drawCell(i.first, i.second, sz, canvas, paintGreen)
                    }
                    i.third > 0 -> {
                        drawCell(i.first, i.second, sz, canvas, paintRed)
                    }
                    else -> {
                        drawCell(i.first, i.second, sz, canvas, paintBlue)
                    }
                }
            }
        }
        if (currentTime - startTime > 1000 && MainActivity.instance.epidemic && MainActivity.instance.drr == 1) {
            MainActivity.instance.move()
            startTime = currentTime
        }
        canvas.restore()
        invalidate()
    }

}