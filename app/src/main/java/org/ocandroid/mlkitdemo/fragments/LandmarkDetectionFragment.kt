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
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.common.FirebaseVisionPoint
import kotlinx.android.synthetic.main.fragment_detector.*
import org.ocandroid.mlkitdemo.R
import org.ocandroid.mlkitdemo.view.ImageWithBounds


class LandmarkDetectionFragment : Fragment() {

  lateinit var imageView: ImageWithBounds

  override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
    return inflater!!.inflate(R.layout.fragment_detector, container, false)
  }

  override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    renderBitmap(ArrayList(), ArrayList())
    run_on_device.visibility = View.GONE
    setUpClickListeners()
  }

  private fun setUpClickListeners() {
    run_on_cloud.setOnClickListener { runCloudLandMarkDetection() }
  }

  private fun runCloudLandMarkDetection() {
    FirebaseVision.getInstance()
      .getVisionCloudLandmarkDetector(getOptions())
      .detectInImage(getVisionImage())
      .addOnSuccessListener { landmarks ->
        val text = StringBuilder()
        for (landmark in landmarks) {
          text.append("Landmark: " + landmark.landmark + "\n")
          text.append("Confidence: " + landmark.confidence + "\n")
          text.append("Entity Id: " + landmark.entityId + "\n")
          for (loc in landmark.locations) {
            text.append("Latitude: " + loc.latitude + "\n")
            text.append("Longitude: " + loc.longitude + "\n")
          }
          text.append("\n")
        }
        renderBitmap(landmarks.mapNotNull { it.boundingBox }, listOf())
        result_text.text = text.toString()
      }
      .addOnFailureListener(onFailure())
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

    image_view_container.addView(imageView)
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
    val EXAMPLE_FILE_NAME = "landmark.jpg"
    val TAG = "LandmarkDetectionFragment"
  }
}