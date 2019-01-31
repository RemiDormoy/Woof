package com.rdo.octo.woof

import android.graphics.*
import android.graphics.drawable.Drawable
import kotlin.math.PI
import kotlin.math.sin

class BlobDrawable(private var paint: Paint, private val width: Int, private val height: Int, private val offset: Int) :
    Drawable() {
    override fun setAlpha(alpha: Int) {
        paint.alpha = alpha
    }

    private val initialyPeak = height.toFloat() * 0.75 - (offset / 3f)

    var yPeakTranslation = 0

    var yPeak = initialyPeak + yPeakTranslation

    private var progress = 0

    fun setProgress(progress: Int) {
        this.progress = progress
        callback?.invalidateDrawable(this)
    }

    override fun draw(canvas: Canvas) {
        val progressInPx = progress.toFloat() / 1000f * width
        val path = Path()
        path.moveTo(0f, 0f)
        val basXWithoutOffset = progressInPx / 2 * (1 + sin(((progress.toFloat() * PI / 1000) - (PI / 2)))).toFloat()
        val baseX = maxOf(/*2*/0f, basXWithoutOffset)
        path.lineTo(baseX, 0f)
        for (i in 0..height) {
            val y = i.toFloat()
            val offsetWantedForButton = offset * (1 - (progress.toFloat() / 1000f))
            val maxWidthOfBlob = progressInPx - baseX + offsetWantedForButton + 20f
            val value = (maxWidthOfBlob * (25f / (25f + ((y / 25f - yPeak / 25f) * (y / 25f - yPeak / 25f))))) + baseX
            path.lineTo(value.toFloat(), y)
        }
        path.lineTo(baseX, height.toFloat())
        path.lineTo(0f, height.toFloat())
        path.close()
        canvas.drawPath(path, paint)
    }

    override fun getIntrinsicWidth() = width

    override fun getIntrinsicHeight() = height

    override fun getOpacity(): Int {
        return PixelFormat.OPAQUE
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        paint.colorFilter = colorFilter
    }

    fun setPaint(paint: Paint) {
        this.paint = paint
    }
}