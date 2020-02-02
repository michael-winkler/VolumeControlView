package com.nmd.volume

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SeekBar
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import com.nmd.volume.util.VolumeUtil


class VolumeControlView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var seekbar: SeekBar? = null
    private var imageView: ImageView? = null

    private companion object {
        var SHOW = true
        var VOLUME_START_POSITION = 50
    }

    fun show(): Boolean {
        return SHOW
    }

    fun show(show: Boolean) {
        SHOW = show
        animateViewVisibility(SHOW)
    }

    /**
     * Show or hide the volume control view.
     */
    @Suppress("MemberVisibilityCanBePrivate")
    var startPosition: Int
        get() = VOLUME_START_POSITION
        set(position) {
            VOLUME_START_POSITION = position
            seekbar?.progress = VOLUME_START_POSITION
        }

    init {
        ViewCompat.setBackground(
            this,
            ContextCompat.getDrawable(context, R.drawable.round_background)
        )
        ViewCompat.setElevation(
            this,
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4f, resources.displayMetrics)
        )

        if (attrs != null) {
            val a = context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.VolumeControlView,
                defStyleAttr,
                0
            )
            try {
                //SHOW = a.getBoolean(R.styleable.VolumeControlView_show, true)
                VOLUME_START_POSITION =
                    a.getInteger(R.styleable.VolumeControlView_volume_start_positon, 50)
            } finally {
                a.recycle()
            }
        }
        initCustomView()
        visibility = if (SHOW) VISIBLE else GONE
        seekbar?.progress = VOLUME_START_POSITION
        if (VOLUME_START_POSITION == 0) {
            VolumeUtil.muteSeekbar(imageView, context)
        }

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width: Int =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 56f, resources.displayMetrics)
                .toInt()
        val height: Int = width.times(4)
        super.onMeasure(
            MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
        )
    }

    override fun onSaveInstanceState(): Parcelable {
        val bundle = Bundle()
        bundle.putBoolean("visibility", SHOW)
        bundle.putParcelable("superState", super.onSaveInstanceState())
        return bundle
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        // 4
        var viewState = state
        if (viewState is Bundle) {
            SHOW = viewState.getBoolean("visibility", true)
            show(SHOW)
            viewState = viewState.getParcelable("superState")
        }
        super.onRestoreInstanceState(viewState)
    }

    private fun animateViewVisibility(visible: Boolean) {
        if (!visible) {
            this.animate().translationX(
                TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    64f,
                    resources.displayMetrics
                )
            )
        } else {
            this.animate().translationX(0f)
        }
    }

    private fun initCustomView() {
        val layoutInflater: LayoutInflater = LayoutInflater.from(context)
        val view: LinearLayout = layoutInflater.inflate(
            R.layout.volume_control_view,
            (context as Activity).findViewById(android.R.id.content),
            false
        ) as LinearLayout

        seekbar = view.findViewById(R.id.seekbar_view)
        imageView = view.findViewById(R.id.image_view)

        VolumeUtil.setColors(context, seekbar, imageView)
        VolumeUtil.setListeners(context, seekbar, imageView)
        addView(view)
    }

}