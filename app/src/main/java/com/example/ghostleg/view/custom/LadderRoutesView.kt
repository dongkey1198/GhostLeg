package com.example.ghostleg.view.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PathMeasure
import android.util.AttributeSet
import android.view.View
import com.example.ghostleg.model.LadderRoute

class LadderRoutesView(context: Context, attr: AttributeSet?) : View(context, attr) {

    private val _ladderRoutes = mutableListOf<LadderRoute>()
    private var _percentage = 0f

    fun initView(ladderRoute: List<LadderRoute>) {
        _ladderRoutes.clear()
        _ladderRoutes.addAll(ladderRoute)
    }

    fun setPercentage(percentage: Float) {
        _percentage = percentage
        invalidate()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.let { drawPaths(it) }
    }

    private fun drawPaths(canvas: Canvas) {
        _ladderRoutes.forEach {
            val path = createPath(it.pathScales)
            val pathLength = PathMeasure(path, false).length
            val total = pathLength - pathLength * _percentage
            val paint = createPaint(it.lineColor, it.stroke)
            paint.pathEffect = DashPathEffect(floatArrayOf(pathLength, pathLength), total)
            canvas.drawPath(path, paint)
        }
    }

    private fun createPath(pathScales: List<Pair<Float, Float>>): Path {
        return Path().apply {
            pathScales.forEachIndexed { index, scale ->
                if (index == 0) {
                    moveTo(scale.first, scale.second)
                } else {
                    lineTo(scale.first, scale.second)
                }
            }
        }
    }

    private fun createPaint(lineColor: Int, stroke: Float): Paint {
        return Paint().apply {
            strokeWidth = stroke
            color = lineColor
            style = Paint.Style.STROKE
        }
    }
}
