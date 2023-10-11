package com.example.ghostleg.view.main.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.example.ghostleg.data.model.Line

class LadderView(context: Context, attr: AttributeSet?) : View(context, attr) {

    private val _verticalLines: MutableList<Line> = mutableListOf()
    private val _horizontalLines: MutableList<Line> = mutableListOf()
    private val paint = Paint()

    fun updateVerticalLines(lines: List<Line>) {
        _verticalLines.apply {
            clear()
            addAll(lines)
        }
        invalidate()
    }

    fun updateHorizontalLines(lines: List<Line>) {
        _horizontalLines.apply {
            clear()
            addAll(lines)
        }
        invalidate()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.let {
            drawVerticalLines(it)
            drawHorizontalLines(it)
        }
    }

    private fun drawVerticalLines(canvas: Canvas) {
        _verticalLines.forEach { line ->
            paint.apply {
                color = line.color
                strokeWidth = line.stroke
            }
            canvas.drawLine(line.startX, line.startY, line.endX, line.endY, paint)
        }
    }

    private fun drawHorizontalLines(canvas: Canvas) {
        _horizontalLines.forEach { line ->
            paint.apply {
                color = line.color
                strokeWidth = line.stroke
            }
            canvas.drawLine(line.startX, line.startY, line.endX, line.endY, paint)
        }
    }
}
