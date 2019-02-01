package com.rdo.octo.woof

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.*
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.animation.AccelerateInterpolator
import android.view.animation.BounceInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.SeekBar
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.LinearLayoutManager

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
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
    private var isOnPoiDetail = false

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
        initRecyclerViews()
    }

    private fun initRecyclerViews() {
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        recyclerViewPoi.layoutManager = linearLayoutManager
        val linearLayoutManagerVertical = LinearLayoutManager(this)
        friendsRecyclerView.layoutManager = linearLayoutManagerVertical
        friendsRecyclerView.alpha = 1f
        recyclerViewPoi.post {
            recyclerViewPoi.translationY = recyclerViewPoi.height.toFloat()
        }
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
                if (name1.text.toString() == "Toby") {
                    picture1.setImageResource(R.drawable.dog1)
                    name1.text = "Milo"
                    picture1.post {
                        motion.progress = 0.5f
                        picture2.setImageResource(R.drawable.dog2)
                    }
                } else {
                    name1.text = "Toby"
                    picture1.setImageResource(R.drawable.dog2)
                    picture1.post {
                        motion.progress = 0.5f
                        picture2.setImageResource(R.drawable.dog1)
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
            paint.shader = LinearGradient(
                0f,
                0f,
                containerBlob.width.toFloat(),
                containerBlob.height.toFloat(),
                ContextCompat.getColor(this, R.color.cornerTopGradient),
                ContextCompat.getColor(this, R.color.cornerBottomGradient),
                Shader.TileMode.MIRROR
            )
            drawableBlob = BlobDrawable(paint, containerBlob.width, containerBlob.height, seekBar2.thumb.intrinsicWidth)
            blobView.background = drawableBlob
        }
        pinkBackgroundContainer.postDelayed({
            pinkBackgroundContainer.translationX = -1f * pinkBackgroundContainer.width
        }, 50)
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
                    friendsRecyclerView.adapter = FriendsAdapter()
                    friendsRecyclerView.animate().alpha(1F).start()
                    yoloyolo.animate().alpha(0F).start()
                    yoloyoloyolo.animate().alpha(0F).start()
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
        when {
            isOnSwipe -> {
                friendsRecyclerView.animate().alpha(0f).start()
                yoloyolo.animate().alpha(1F).start()
                yoloyoloyolo.animate().alpha(1F).start()
                seekBar2.animate().alpha(1f).start()
                moveBlobToZero(1000)
            }
            isOnPoiDetail -> hidePoiDetail()
            else -> super.onBackPressed()
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

    private fun hidePoiDetail() {
        recyclerViewPoi.animate().translationY(recyclerViewPoi.height.toFloat() * 1.2f).setDuration(800)
            .setInterpolator(AccelerateInterpolator()).start()
        alphaView.animate().alpha(0f).setDuration(500).start()
        alphaView.isClickable = false
        isOnPoiDetail = false
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
        mMap.setOnMarkerClickListener {
            recyclerViewPoi.adapter = PoiAdapter()
            recyclerViewPoi.animate().translationY(0f).setDuration(800).setInterpolator(DecelerateInterpolator())
                .start()
            alphaView.animate().alpha(0.3f).setDuration(500).start()
            alphaView.isClickable = true
            alphaView.setOnClickListener { hidePoiDetail() }
            isOnPoiDetail = true
            true
        }

        // Add a marker in Sydney and move the camera
        var sydney = LatLng(48.8620528, 2.3287441)
        mMap.addMarker(MarkerOptions().position(sydney).icon(BitmapDescriptorFactory.fromBitmap(getBitmapFromVectorDrawable(R.drawable.ic_pin_beauty))))
        sydney = LatLng(48.8637884, 2.3226724)
        mMap.addMarker(MarkerOptions().position(sydney).icon(BitmapDescriptorFactory.fromBitmap(getBitmapFromVectorDrawable(R.drawable.ic_pin_clean))))
        sydney = LatLng(48.865466, 2.326013)
        mMap.addMarker(MarkerOptions().position(sydney).icon(BitmapDescriptorFactory.fromBitmap(getBitmapFromVectorDrawable(R.drawable.ic_pin_formation))))
        sydney = LatLng(48.8599614, 2.3243727)
        mMap.addMarker(MarkerOptions().position(sydney).icon(BitmapDescriptorFactory.fromBitmap(getBitmapFromVectorDrawable(R.drawable.ic_pin_meeting))))
        sydney = LatLng(48.8630257, 2.3270115)
        mMap.addMarker(MarkerOptions().position(sydney).icon(BitmapDescriptorFactory.fromBitmap(getBitmapFromVectorDrawable(R.drawable.ic_pin_run))))
        val newLatLng = CameraUpdateFactory.newLatLngZoom(sydney, 15f)
        mMap.moveCamera(newLatLng)
    }
}

fun Context.getBitmapFromVectorDrawable(drawableId: Int): Bitmap? {
    var drawable = ContextCompat.getDrawable(this, drawableId) ?: return null

    val bitmap = Bitmap.createBitmap(
        Math.round(drawable.intrinsicWidth * 0.80f),
        Math.round(drawable.intrinsicHeight * 0.80f),
        Bitmap.Config.ARGB_8888) ?: return null
    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)

    return bitmap
}
