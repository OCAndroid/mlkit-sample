package org.ocandroid.mlkitdemo

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
    canvas.drawPoint(point.x.scaleToFactor(), point.y.scaleToFactor(), dotPaint)
  }

  private fun drawRect(canvas: Canvas, rect: Rect) {
    drawBottomBorder(canvas, rect.scaleToFactor(), linePaint)
    drawTopBorder(canvas, rect.scaleToFactor(), linePaint)
    drawLeftBorder(canvas, rect.scaleToFactor(), linePaint)
    drawRightBorder(canvas, rect.scaleToFactor(), linePaint)
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

  companion object {
    const val SCALE_FACTOR: Float = 0.92f
  }

}

// Helper method used to scale the bounding box to the right dimensions when drawn.
fun Rect.scaleToFactor() = Rect(
  (this.left * ImageWithBounds.SCALE_FACTOR).toInt(),
  (this.top * ImageWithBounds.SCALE_FACTOR).toInt(),
  (this.right * ImageWithBounds.SCALE_FACTOR).toInt(),
  (this.bottom * ImageWithBounds.SCALE_FACTOR).toInt()
)

// Helper method used to scale the dot to the correct dimensions when drawn.
fun Float.scaleToFactor() = ImageWithBounds.SCALE_FACTOR * this