package com.rdo.octo.woof

import android.animation.Animator
import android.animation.ObjectAnimator
import android.graphics.Paint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.animation.BounceInterpolator
import android.widget.SeekBar
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.content.ContextCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_maps.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    private lateinit var drawableBlob: BlobDrawable

    private var isImageTouched = false

    private lateinit var mapFragment: SupportMapFragment

    private var initialYPeak = -1

    private val initialSeekBarPosition: Int by lazy { seekBar2.y.toInt() }

    private var isOnSwipe = false

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        strokeWidth = 4f
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        stuffForBlob()
        stuffForSwipe()
    }

    private fun stuffForSwipe() {
        motion.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> isImageTouched = true
                MotionEvent.ACTION_UP -> isImageTouched = false
            }
            false
        }
        motion.setTransitionListener(object : MotionLayout.TransitionListener {
            override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {
                // Do nothing
            }

            override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {
                // Do nothing
            }

            override fun onTransitionChange(p0: MotionLayout, startSet: Int, endSet: Int, progress: Float) {
                if (!isImageTouched) {
                    if (progress < 0.55 && progress > 0.4) {
                        p0.progress = 0.5f
                    }
                }
            }

            override fun onTransitionCompleted(p0: MotionLayout, constraintSet: Int) {
                if (name1.text.toString() == "Paris") {
                    picture1.setImageResource(R.drawable.sanfrancisco)
                    name1.text = "New York"
                    picture1.post {
                        motion.progress = 0.5f
                        name2.text = "Paris"
                        picture2.setImageResource(R.drawable.paris)
                    }
                } else {
                    name1.text = "Paris"
                    picture1.setImageResource(R.drawable.paris)
                    picture1.post {
                        motion.progress = 0.5f
                        name2.text = "New York"
                        picture2.setImageResource(R.drawable.sanfrancisco)
                    }
                }
            }

        })
    }

    private fun stuffForBlob() {
        paint.color = ContextCompat.getColor(this, R.color.colorAccent)
        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        containerBlob.post {
            drawableBlob = BlobDrawable(paint, containerBlob.width, containerBlob.height, seekBar2.thumb.intrinsicWidth)
            blobView.background = drawableBlob
        }
        pinkBackgroundContainer.post {
            pinkBackgroundContainer.translationX = -1f * pinkBackgroundContainer.width
        }
        seekBar2.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                drawableBlob.setProgress(progress)
                val progressInPx = progress.toFloat() / 1000f * containerBlob.width
                pinkBackgroundContainer.translationX = -1f * pinkBackgroundContainer.width + progressInPx
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // Do nothing for now
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                val progress = seekBar2.progress
                if (progress > 500) {
                    val animator = ObjectAnimator.ofInt(progress, 1000)
                    animator.duration = 300
                    animator.interpolator = BounceInterpolator()
                    animator.addUpdateListener {
                        val value = it.animatedValue as Int
                        seekBar2.progress = value
                    }
                    animator.start()
                    isOnSwipe = true
                    seekBar2.animate().alpha(0f).start()
                } else {
                    moveBlobToZero(progress)
                }
            }

        })
        seekBar2.setOnTouchListener { v, event ->
            val newYpeak = (event.rawY - seekBar2.height).toDouble()
            drawableBlob.yPeak = newYpeak
            seekBar2.translationY = newYpeak.toFloat() - initialSeekBarPosition - (seekBar2.height / 2)
            false
        }
    }

    override fun onBackPressed() {
        if (isOnSwipe) {
            seekBar2.animate().alpha(1f).start()
            moveBlobToZero(1000)
        } else {
            super.onBackPressed()
        }
    }

    private fun moveBlobToZero(progress: Int) {
        val animator = ObjectAnimator.ofInt(progress, 0)
        animator.duration = 300
        animator.interpolator = BounceInterpolator()
        animator.addUpdateListener {
            val value = it.animatedValue as Int
            seekBar2.progress = value
        }
        animator.start()
        isOnSwipe = false
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(48.8630257, 2.3270115)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        val newLatLng = CameraUpdateFactory.newLatLngZoom(sydney, 13f)
        mMap.moveCamera(newLatLng)
    }
}
