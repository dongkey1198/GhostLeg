package com.example.ghostleg.view.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.example.ghostleg.model.Line

class LadderView(context: Context, attr: AttributeSet?): View(context, attr) {

    private val _verticalLines: MutableList<Line> = mutableListOf()
    private val paint = Paint()

    fun updateVerticalLines(lines: List<Line>) {
        _verticalLines.apply {
            clear()
            addAll(lines)
        }
        invalidate()
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.let {
            drawVerticalLines(it)
        }
    }

    private fun drawVerticalLines(canvas: Canvas) {
        _verticalLines.forEach { ladder ->
            paint.apply {
                color = ladder.color
                strokeWidth = ladder.stroke
            }
            canvas.drawLine(ladder.startX, ladder.startY, ladder.endX, ladder.endY, paint)
        }
    }
}
