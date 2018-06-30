package org.ocandroid.mlkitdemo

import android.app.Fragment
import android.app.FragmentTransaction
import android.graphics.Bitmap
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import android.support.annotation.NonNull
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.ml.vision.text.FirebaseVisionText
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.text.FirebaseVisionTextDetector
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import android.text.method.TextKeyListener.clear
import android.widget.Toast


class MainActivity : AppCompatActivity(), MenuFragment.FragmentHandler {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    addFragment(MenuFragment(), MenuFragment.TAG)
  }

  override fun setCurrentFragment(fragment: Fragment, tag: String) {
    pushFragment(fragment, tag)
  }

  private fun addFragment(fragment: Fragment, tag: String) =
    fragmentManager
      .beginTransaction()
      .add(R.id.main_container, fragment, tag)
      .commit()

  private fun pushFragment(fragment: Fragment, tag: String) =
    fragmentManager
      .beginTransaction()
      .replace(R.id.main_container, fragment, tag)
      .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
      .addToBackStack(tag)
      .commit()
}
