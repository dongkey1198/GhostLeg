package com.example.ghostleg.view.main.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.example.ghostleg.data.model.Ladder

class LadderView(context: Context, attr: AttributeSet?) : View(context, attr) {

    private val _verticalLadders: MutableList<Ladder> = mutableListOf()
    private val _horizontalLadders: MutableList<Ladder> = mutableListOf()
    private val paint = Paint()

    fun updateVerticalLines(ladders: List<Ladder>) {
        _verticalLadders.apply {
            clear()
            addAll(ladders)
        }
        invalidate()
    }

    fun updateHorizontalLines(ladders: List<Ladder>) {
        _horizontalLadders.apply {
            clear()
            addAll(ladders)
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
        _verticalLadders.forEach { line ->
            paint.apply {
                color = line.color
                strokeWidth = line.stroke
            }
            canvas.drawLine(line.startX, line.startY, line.endX, line.endY, paint)
        }
    }

    private fun drawHorizontalLines(canvas: Canvas) {
        _horizontalLadders.forEach { line ->
            paint.apply {
                color = line.color
                strokeWidth = line.stroke
            }
            canvas.drawLine(line.startX, line.startY, line.endX, line.endY, paint)
        }
    }
}
