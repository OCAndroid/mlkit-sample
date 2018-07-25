package org.ocandroid.mlkitdemo.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.widget.ImageView
import com.google.firebase.ml.vision.common.FirebaseVisionPoint

class ImageWithBounds @JvmOverloads constructor(
  context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ImageView(context, attrs, defStyleAttr) {

  var rects: List<Rect> = ArrayList()
  var landmarks: List<FirebaseVisionPoint> = ArrayList()

  val linePaint: Paint by lazy {
    val linePaint = Paint()
    linePaint.color = 0xFFFF0000.toInt()
    linePaint.style = Paint.Style.FILL_AND_STROKE
    linePaint.strokeWidth = 5f
    linePaint
  }

  val dotPaint: Paint by lazy {
    val dotPaint = Paint()
    dotPaint.color = 0xFF00FF00.toInt()
    dotPaint.style = Paint.Style.FILL_AND_STROKE
    dotPaint.strokeWidth = 5f
    dotPaint
  }

  override fun onDraw(canvas: Canvas?) {
    super.onDraw(canvas)
    if (canvas != null) {
      rects.forEach { drawRect(canvas, it) }
      landmarks.forEach { drawDot(canvas, it) }
      canvas.save()
    }
  }

  private fun drawDot(canvas: Canvas, point: FirebaseVisionPoint) {
    val amount = canvas.factor()
    canvas.drawPoint(point.x.scale(amount), point.y.scale(amount), dotPaint)
  }

  private fun drawRect(canvas: Canvas, rect: Rect) {
    val amount = canvas.factor()
    drawBottomBorder(canvas, rect.scale(amount), linePaint)
    drawTopBorder(canvas, rect.scale(amount), linePaint)
    drawLeftBorder(canvas, rect.scale(amount), linePaint)
    drawRightBorder(canvas, rect.scale(amount), linePaint)
  }

  private fun drawTopBorder(canvas: Canvas, rect: Rect, paint: Paint) {
    canvas.drawLine(rect.left.toFloat(), rect.top.toFloat(), rect.right.toFloat(), rect.top.toFloat(), paint)
  }

  private fun drawLeftBorder(canvas: Canvas, rect: Rect, paint: Paint) {
    canvas.drawLine(rect.left.toFloat(), rect.top.toFloat(), rect.left.toFloat(), rect.bottom.toFloat(), paint)
  }

  private fun drawRightBorder(canvas: Canvas, rect: Rect, paint: Paint) {
    canvas.drawLine(rect.right.toFloat(), rect.top.toFloat(), rect.right.toFloat(), rect.bottom.toFloat(), paint)
  }

  private fun drawBottomBorder(canvas: Canvas, rect: Rect, paint: Paint) {
    canvas.drawLine(rect.left.toFloat(), rect.bottom.toFloat(), rect.right.toFloat(), rect.bottom.toFloat(), paint)
  }

  fun Canvas.factor() = 1.0f * this.width / drawable.bounds.right

  // Helper method used to scale the bounding box to the right dimensions when drawn.
  fun Rect.scale(factor: Float) = Rect(
    (this.left * factor).toInt(),
    (this.top * factor).toInt(),
    (this.right * factor).toInt(),
    (this.bottom * factor).toInt()
  )

  // Helper method used to scale the dot to the correct dimensions when drawn.
  fun Float.scale(factor: Float) = factor * this
}

