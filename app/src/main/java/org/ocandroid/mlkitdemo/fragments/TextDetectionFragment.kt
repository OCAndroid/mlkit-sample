package org.ocandroid.mlkitdemo.fragments

import android.app.Fragment
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.cloud.FirebaseVisionCloudDetectorOptions
import com.google.firebase.ml.vision.cloud.text.FirebaseVisionCloudText
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.common.FirebaseVisionPoint
import kotlinx.android.synthetic.main.fragment_detector.*
import org.ocandroid.mlkitdemo.R
import org.ocandroid.mlkitdemo.view.ImageWithBounds

class TextDetectionFragment: Fragment() {

  lateinit var imageView: ImageWithBounds

  override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
    return inflater!!.inflate(R.layout.fragment_detector, container, false)
  }

  override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    renderBitmap(ArrayList(), ArrayList())
    setUpClickListeners()
  }

  private fun setUpClickListeners() {
    run_on_device.setOnClickListener { runDeviceTextDetection() }
    run_on_cloud.setOnClickListener { runCloudTextDetection() }
  }


  private fun runDeviceTextDetection() {
    FirebaseVision.getInstance()
      .getVisionTextDetector()
      .detectInImage(getVisionImage())
      .addOnSuccessListener { textResult ->
        renderBitmap(textResult.blocks.flatMap { it.lines.map { it.boundingBox } .filterNotNull() }, arrayListOf())
        val text = StringBuilder()
        for (block in textResult.blocks) {
          for (line in block.lines) {
            text.append("BoundingBox: " + line.boundingBox + "\n")
            text.append("Elements: " + line.elements.map { it.text }.joinToString("+") + "\n")
          }
        }
        result_text.text = text.toString()
      }
      .addOnFailureListener(onFailure())
  }

  private fun runCloudTextDetection() {
    FirebaseVision.getInstance()
      .getVisionCloudDocumentTextDetector()
      .detectInImage(getVisionImage())
      .addOnSuccessListener { textResult ->
        renderBitmap(getSymbolBoundingBoxes(textResult), arrayListOf())
        val text = StringBuilder()
        for (page in textResult.pages) {
          for (block in page.blocks) {
            for (paragraph in block.paragraphs) {
              for (word in paragraph.words) {
                text.append("BoundingBox: " + word.boundingBox + "\n")
              }
              text.append("Elements: " + paragraph.words.mapNotNull { it.symbols.mapNotNull { it.text }.joinToString("") }.joinToString("+") + "\n")
            }
          }
        }
        result_text.text = text.toString()
      }
      .addOnFailureListener(onFailure())
  }


  private fun getSymbolBoundingBoxes(textResult: FirebaseVisionCloudText) =
    textResult.pages.flatMap{
      it.blocks.flatMap {
      it.paragraphs.flatMap {
      it.words.flatMap {
      it.symbols.mapNotNull {
      it.boundingBox } } } }
    }

  private fun onFailure() = OnFailureListener {
    Log.e(TAG, "Failed to detect landmark", it)
  }


  private fun renderBitmap(markers: List<Rect>, landmarks: List<FirebaseVisionPoint>) {
    image_view_container.removeAllViews() // clean-up

    val bitmap = BitmapFactory.decodeStream(context.assets.open(EXAMPLE_FILE_NAME))
    imageView = ImageWithBounds(context)
    imageView.adjustViewBounds = true
    imageView.rects = markers
    imageView.landmarks = landmarks
    imageView.setImageBitmap(bitmap)

    Log.i(TAG, "Width: ${bitmap.width} & ${bitmap.height}")
    image_view_container.addView(imageView)
    Log.i(TAG, "Width: ${image_view_container.width} & ${image_view_container.height}")
    Log.i(TAG, "Width: ${image_view_container.width.toFloat() / bitmap.width.toFloat()}")
  }



  private fun getOptions() = FirebaseVisionCloudDetectorOptions.Builder()
    .setModelType(FirebaseVisionCloudDetectorOptions.LATEST_MODEL)
    .setMaxResults(15)
    .build()

  private fun getVisionImage() =
    FirebaseVisionImage.fromBitmap(
      BitmapFactory.decodeStream(
        context.assets.open(EXAMPLE_FILE_NAME)
      )
    )

  companion object {
    val EXAMPLE_FILE_NAME = "text_image.png"
    val TAG = "TextDetectionFragment"
  }
}