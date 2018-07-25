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
import com.google.firebase.ml.vision.cloud.label.FirebaseVisionCloudLabel
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.common.FirebaseVisionPoint
import com.google.firebase.ml.vision.label.FirebaseVisionLabel
import com.google.firebase.ml.vision.label.FirebaseVisionLabelDetectorOptions
import kotlinx.android.synthetic.main.fragment_detector.*
import org.ocandroid.mlkitdemo.R
import org.ocandroid.mlkitdemo.view.ImageWithBounds


class ImageLabelingFragment : Fragment() {

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
    run_on_device.setOnClickListener { runImageLabelingDevice() }
    run_on_cloud.setOnClickListener { runImageLabelingCloud() }
  }

  private fun renderBitmap(markers: List<Rect>, landmarks: List<FirebaseVisionPoint>) {
    image_view_container.removeAllViews() // clean-up

    val bitmap = BitmapFactory.decodeStream(context.assets.open(FaceDetectionFragment.EXAMPLE_FILE_NAME))
    imageView = ImageWithBounds(context)
    imageView.adjustViewBounds = true
    imageView.rects = markers
    imageView.landmarks = landmarks
    imageView.setImageBitmap(bitmap)

    image_view_container.addView(imageView)
  }

  private fun runImageLabelingDevice() {
    val image = FirebaseVisionImage.fromBitmap(getBitmap())
    FirebaseVision.getInstance()
      .getVisionLabelDetector(getOptions())
      .detectInImage(image)
      .addOnSuccessListener({ onDeviceSuccess(it) })
      .addOnFailureListener(onFailure())
  }

  private fun runImageLabelingCloud() {
    val image = FirebaseVisionImage.fromBitmap(getBitmap())
    FirebaseVision.getInstance()
      .getVisionCloudLabelDetector()
      .detectInImage(image)
      .addOnSuccessListener({ onCloudSuccess(it) })
      .addOnFailureListener(onFailure())

  }

  private fun onFailure() = OnFailureListener {
    result_text.text = "Image Labeling Failed"
  }

  private fun onCloudSuccess(list: List<FirebaseVisionCloudLabel>) {
    val text = StringBuilder()
    for (label in list) {
      text.append("Labeling Result Text: ", label.label + "\n")
      text.append("Labeling Result ID: ", label.entityId + "\n")
      text.append("Labeling Result Score: ", label.confidence.toString() + "\n\n")
    }
    result_text.text = text.toString()
  }

  private fun onDeviceSuccess(list: List<FirebaseVisionLabel>) {
    val text = StringBuilder()
    for (label in list) {
      text.append("Labeling Result Text: ", label.label + "\n")
      text.append("Labeling Result ID: ", label.entityId + "\n")
      text.append("Labeling Result Score: ", label.confidence.toString() + "\n\n")
    }
    result_text.text = text.toString()
  }

  private fun getOptions() = FirebaseVisionLabelDetectorOptions.Builder()
    .setConfidenceThreshold(0.8f)
    .build()

  private fun getBitmap() = BitmapFactory
    .decodeStream(context.assets.open(FaceDetectionFragment.EXAMPLE_FILE_NAME))

  companion object {
    const val TAG = "ImageLabelingFragment"
  }
}