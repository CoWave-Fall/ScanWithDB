package sys.tianr.test.scanwithdb.ui.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

/**
 * 一个自定义视图，用于在相机预览上绘制一个扫描取景框。
 */
class ViewfinderView(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    private val boxPaint: Paint = Paint().apply {
        color = Color.WHITE
        style = Paint.Style.STROKE
        strokeWidth = 5f
    }

    private val cornerPaint: Paint = Paint().apply {
        color = Color.WHITE
        style = Paint.Style.STROKE
        strokeWidth = 15f
    }

    private var boxRect: RectF? = null
    private val cornerLength = 60f

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (boxRect == null) {
            val viewWidth = width.toFloat()
            val viewHeight = height.toFloat()
            // 创建一个位于视图中心的正方形取景框
            val boxSize = viewWidth * 0.75f
            val left = (viewWidth - boxSize) / 2
            val top = (viewHeight - boxSize) / 2
            val right = left + boxSize
            val bottom = top + boxSize
            boxRect = RectF(left, top, right, bottom)
        }

        boxRect?.let { rect ->
            // 绘制四个角的标记
            // Top-left
            canvas.drawLine(rect.left, rect.top + cornerLength, rect.left, rect.top, cornerPaint)
            canvas.drawLine(rect.left, rect.top, rect.left + cornerLength, rect.top, cornerPaint)
            // Top-right
            canvas.drawLine(rect.right - cornerLength, rect.top, rect.right, rect.top, cornerPaint)
            canvas.drawLine(rect.right, rect.top, rect.right, rect.top + cornerLength, cornerPaint)
            // Bottom-left
            canvas.drawLine(rect.left, rect.bottom - cornerLength, rect.left, rect.bottom, cornerPaint)
            canvas.drawLine(rect.left, rect.bottom, rect.left + cornerLength, rect.bottom, cornerPaint)
            // Bottom-right
            canvas.drawLine(rect.right - cornerLength, rect.bottom, rect.right, rect.bottom, cornerPaint)
            canvas.drawLine(rect.right, rect.bottom, rect.right, rect.bottom - cornerLength, cornerPaint)
        }
    }

    /**
     * 设置取景框和角落标记的颜色。
     * @param color 要设置的颜色值。
     */
    fun setViewfinderColor(color: Int) {
        cornerPaint.color = color
        invalidate() // 请求重绘视图
    }
}
