package org.ocandroid.mlkitdemo;

import android.app.Activity
import android.app.Fragment
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_learning_menu.*

class MenuFragment: Fragment() {

    private lateinit var fragmentHandler: FragmentHandler

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater!!.inflate(R.layout.fragment_learning_menu, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        barcode_scanning.setOnClickListener(startBarcodeScanning())
        face_detection.setOnClickListener(startFacialRecognition())
        image_labeling.setOnClickListener(startImageLabeling())
        landmark_detection.setOnClickListener(startLandmarkDetection())
        text_recognition.setOnClickListener(startTextRecognition())
    }


    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        fragmentHandler = activity as FragmentHandler
    }

    fun startFacialRecognition() = View.OnClickListener {
        Log.d("OnClickListener", "Facial Recognition!")
        fragmentHandler.setCurrentFragment(FaceDetectionFragment(), FaceDetectionFragment.TAG)
    }

    fun startLandmarkDetection() = View.OnClickListener {
        Log.d("OnClickListener", "Landmark Detection!")
    }

    fun startTextRecognition() = View.OnClickListener {
        Log.d("OnClickListener", "Text Recognition!")
    }

    fun startBarcodeScanning() = View.OnClickListener {
        Log.d("OnClickListener", "Barcode Scanning!")
    }

    fun startImageLabeling() = View.OnClickListener {
        Log.d("OnClickListener", "Image Labeling!")
    }

    interface FragmentHandler {
        fun setCurrentFragment(fragment: Fragment, tag: String)
    }

    companion object {
        val TAG = "MenuFragment"
    }
}

