package com.rdo.octo.woof

import android.animation.Animator
import android.animation.ObjectAnimator
import android.graphics.Paint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.BounceInterpolator
import android.widget.SeekBar
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

    private lateinit var mapFragment: SupportMapFragment

    private var initialYPeak = -1

    private val initialSeekBarPosition: Int by lazy { seekBar2.y.toInt() }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        strokeWidth = 4f
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
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
                    animator.addListener(object : Animator.AnimatorListener {
                        override fun onAnimationRepeat(animation: Animator?) {
                            // Do nothing
                        }

                        override fun onAnimationEnd(animation: Animator?) {
                            seekBar2.postDelayed({
                                val animatorFade = ObjectAnimator.ofFloat(0f, 2f)
                                animatorFade.duration = 1000
                                animatorFade.addUpdateListener {
                                    val value = it.animatedValue as Float
                                    if (value < 1f) {
                                        containerBlob.alpha = 1f - value
                                    } else {
                                        containerBlob.alpha = value - 1f
                                        seekBar2.progress = 0
                                    }
                                }
                                animatorFade.start()
                            }, 3000)
                        }

                        override fun onAnimationCancel(animation: Animator?) {
                            // Do nothing
                        }

                        override fun onAnimationStart(animation: Animator?) {
                            // Do nothing
                        }

                    })
                    animator.start()
                } else {
                    val animator = ObjectAnimator.ofInt(progress, 0)
                    animator.duration = 300
                    animator.interpolator = BounceInterpolator()
                    animator.addUpdateListener {
                        val value = it.animatedValue as Int
                        seekBar2.progress = value
                    }
                    animator.start()
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
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }
}
