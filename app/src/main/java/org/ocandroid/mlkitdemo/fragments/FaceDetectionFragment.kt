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
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.common.FirebaseVisionPoint
import com.google.firebase.ml.vision.face.FirebaseVisionFace
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions
import com.google.firebase.ml.vision.face.FirebaseVisionFaceLandmark
import kotlinx.android.synthetic.main.fragment_detector.*
import org.ocandroid.mlkitdemo.R
import org.ocandroid.mlkitdemo.view.ImageWithBounds


class FaceDetectionFragment : Fragment() {

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
    run_on_device.setOnClickListener { runFaceRecognition(FirebaseVisionFaceDetectorOptions.FAST_MODE) }
    run_on_cloud.setOnClickListener { runFaceRecognition(FirebaseVisionFaceDetectorOptions.ACCURATE_MODE) }
  }

  private fun runFaceRecognition(mode: Int) {
    error.visibility = View.GONE
    FirebaseVision
      .getInstance()
      .getVisionFaceDetector(getOptions(mode))
      .detectInImage(getVisionImage())
      .addOnSuccessListener(onSuccess())
      .addOnFailureListener(onFailure())
  }

  private fun onFailure() = OnFailureListener { e ->
    error.visibility = View.VISIBLE
    Log.e(TAG, "Failed to Recognize", e)
  }

  private fun onSuccess() = OnSuccessListener<List<FirebaseVisionFace>> { faces ->
    Log.d("Done", "Got Back ${faces.size} results")
    renderBitmap(faces.map { it.boundingBox }, faces.flatMap { findAllLandmarks(it) })
    faces.forEach { logAdditionalInformation(it) }
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

  ////////////////////////////////////////////////////////////////////////////////////////////////
  // Start Helper Methods
  ////////////////////////////////////////////////////////////////////////////////////////////////

  private fun getOptions(mode: Int) =
    FirebaseVisionFaceDetectorOptions.Builder()
      .setModeType(mode) // Cloud = Accurate vs Fast = Device
      .setLandmarkType(FirebaseVisionFaceDetectorOptions.ALL_LANDMARKS)
      .setClassificationType(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
      .setMinFaceSize(0.15f)
      .setTrackingEnabled(true)
      .build()

  private fun getVisionImage() =
    FirebaseVisionImage.fromBitmap(
      BitmapFactory.decodeStream(
        context.assets.open(EXAMPLE_FILE_NAME)
      )
    )

  private fun findAllLandmarks(result: FirebaseVisionFace): List<FirebaseVisionPoint> {
    return arrayOf(
      result.getLandmark(FirebaseVisionFaceLandmark.LEFT_EAR),
      result.getLandmark(FirebaseVisionFaceLandmark.RIGHT_EAR),
      result.getLandmark(FirebaseVisionFaceLandmark.LEFT_MOUTH),
      result.getLandmark(FirebaseVisionFaceLandmark.RIGHT_MOUTH),
      result.getLandmark(FirebaseVisionFaceLandmark.BOTTOM_MOUTH),
      result.getLandmark(FirebaseVisionFaceLandmark.LEFT_CHEEK),
      result.getLandmark(FirebaseVisionFaceLandmark.RIGHT_CHEEK),
      result.getLandmark(FirebaseVisionFaceLandmark.NOSE_BASE),
      result.getLandmark(FirebaseVisionFaceLandmark.LEFT_EYE),
      result.getLandmark(FirebaseVisionFaceLandmark.RIGHT_EYE)
    )
      .filterNotNull()
      .map { it.position }
  }

  private fun logAdditionalInformation(face: FirebaseVisionFace) {
    Log.d(TAG, "boundingBox:" + face.boundingBox.toString())
    Log.d(TAG, "headEulerAngleY:" + face.headEulerAngleY.toString()) // Head is rotated to the right rotY degrees
    Log.d(TAG, "headEulerAngleZ:" + face.headEulerAngleZ.toString()) // Head is tilted sideways rotZ degrees

    // If landmark detection was enabled (mouth, ears, eyes, cheeks, and
    // nose available):
    val leftEar = face.getLandmark(FirebaseVisionFaceLandmark.LEFT_EAR)
    if (leftEar != null) {
      val leftEarPos = leftEar.position
      Log.d(TAG, "leftEar:" + leftEar.position.toString())
    }

    // If classification was enabled:
    if (face.smilingProbability !== FirebaseVisionFace.UNCOMPUTED_PROBABILITY) {
      val smileProb = face.smilingProbability
      Log.d(TAG, "smileProb:" + smileProb.toString())
    }
    if (face.rightEyeOpenProbability !== FirebaseVisionFace.UNCOMPUTED_PROBABILITY) {
      val rightEyeOpenProb = face.rightEyeOpenProbability
      Log.d(TAG, "rightEyeOpenProb:" + rightEyeOpenProb.toString())
    }

    // If face tracking was enabled:
    if (face.trackingId !== FirebaseVisionFace.INVALID_ID) {
      val id = face.trackingId
      Log.d(TAG, "id:" + id.toString())
    }
  }


  companion object {
    val EXAMPLE_FILE_NAME = "moi_et_ma_femme.jpg"
    val TAG = "FaceDetectionFragment"
  }
}