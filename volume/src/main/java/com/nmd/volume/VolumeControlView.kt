package com.nmd.volume


import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.TypedValue
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.SeekBar
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.ViewCompat


class VolumeControlView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var seekbar: SeekBar? = null
    private var imageView: ImageView? = null
    private var extraMargin = 0f
    private val handlerTask = Handler(Looper.getMainLooper())
    private var musicOn = true
    private var volume_thumb_color = R.color.thumb_color
    private var volume_thumb_progress_color = R.color.thumb_progress_color
    private var volume_icon_color = R.color.icon_color

    private companion object {
        var SHOW = true
        var VOLUME_START_POSITION = 50
    }

    fun show(): Boolean {
        return SHOW
    }

    fun show(show: Boolean) {
        SHOW = show
        if (SHOW) {
            timerStartHide()
        } else {
            timerStopHide()
        }
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

        addView(inflate(context, R.layout.volume_control_view, null))

        if (attrs != null) {
            val a = context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.VolumeControlView,
                defStyleAttr,
                0
            )

            try {
                SHOW = a.getBoolean(R.styleable.VolumeControlView_show, true)
                VOLUME_START_POSITION =
                    a.getInteger(R.styleable.VolumeControlView_volume_start_positon, 50)
                volume_thumb_color = a.getColor(
                    R.styleable.VolumeControlView_volume_thumb_color,
                    ContextCompat.getColor(context, R.color.thumb_color)
                )
                volume_thumb_progress_color = a.getColor(
                    R.styleable.VolumeControlView_volume_thumb_progress_color,
                    ContextCompat.getColor(context, R.color.thumb_progress_color)
                )
                volume_icon_color = a.getColor(
                    R.styleable.VolumeControlView_volume_icon_color,
                    ContextCompat.getColor(context, R.color.icon_color)
                )
            } finally {
                a.recycle()
            }
        }

        if (!SHOW) {
            x =
                TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    56f.plus(extraMargin),
                    resources.displayMetrics
                )
        }

        seekbar = findViewById(R.id.seekbar_view)
        imageView = findViewById(R.id.image_view)

        seekbar?.progress = VOLUME_START_POSITION
        if (VOLUME_START_POSITION == 0) {
            muteSeekbar(imageView, context)
        }

        setColors(context, seekbar, imageView)
        setListeners(context, seekbar, imageView)

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

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        val marginLayoutParams = MarginLayoutParams::class.java.cast(layoutParams)
        val marginRight = marginLayoutParams?.rightMargin ?: 0
        extraMargin =
            marginRight / (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    }

    /*
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
     */

    fun setListeners(context: Context?, seekBar: SeekBar?, imageView: ImageView?) {
        var lastPosition: Int = seekBar?.progress ?: 50
        context?.let {

            val musicOnDrawable: Drawable? = ContextCompat.getDrawable(it, R.drawable.music_on)
            val musicOffDrawable: Drawable? =
                ContextCompat.getDrawable(it, R.drawable.music_off)

            if (musicOnDrawable != null) {
                DrawableCompat.setTint(musicOnDrawable, volume_icon_color)
            }

            if (musicOffDrawable != null) {
                DrawableCompat.setTint(musicOffDrawable, volume_icon_color)
            }

            seekBar?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    if (progress == 0) {
                        musicOn = false
                        imageView?.setImageDrawable(musicOffDrawable)

                    } else {
                        if (!musicOn) {
                            imageView?.setImageDrawable(musicOnDrawable)
                        }
                        musicOn = true
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {

                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {

                }
            })

            imageView?.setOnClickListener {
                if (musicOn) {
                    lastPosition = seekBar?.progress ?: 50
                    animateProgressChange(seekBar, true, lastPosition)
                    imageView.setImageDrawable(musicOffDrawable)
                } else {
                    animateProgressChange(seekBar, false, lastPosition)
                    imageView.setImageDrawable(musicOnDrawable)
                }
                timerStopHide()
                timerStartHide()
            }
        }

    }

    fun muteSeekbar(imageView: ImageView?, context: Context?) {
        context?.let {
            musicOn = false
            val musicOffDrawable: Drawable? =
                ContextCompat.getDrawable(context, R.drawable.music_off)
            if (musicOffDrawable != null) {
                DrawableCompat.setTint(musicOffDrawable, volume_icon_color)
            }
            imageView?.setImageDrawable(musicOffDrawable)
        }
    }

    private fun animateProgressChange(seekBar: SeekBar?, musicOff: Boolean, lastPosition: Int) {
        seekBar?.let {
            val anim: ObjectAnimator = if (musicOff) {
                ObjectAnimator.ofInt(seekBar, "progress", it.progress, 0)
            } else {
                ObjectAnimator.ofInt(seekBar, "progress", 0, lastPosition)
            }

            anim.duration = 300
            anim.start()
        }

    }

    fun setColors(context: Context?, seekBar: SeekBar?, imageView: ImageView?) {
        context?.let {
            seekBar?.let {
                DrawableCompat.setTint(
                    it.thumb, volume_thumb_color
                )
                DrawableCompat.setTint(
                    it.progressDrawable, volume_thumb_progress_color
                )
            }

            imageView?.let {
                DrawableCompat.setTint(
                    it.drawable, volume_icon_color
                )
            }
        }
    }

    private fun animateViewVisibility(visible: Boolean) {
        if (!visible) {
            animate().translationX(
                TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    56f.plus(extraMargin),
                    resources.displayMetrics
                )
            )
        } else {
            animate().translationX(0f)
        }
    }

    private fun timerStartHide() {
        handlerTask.postDelayed({
            show(false)
        }, 3000)
    }

    private fun timerStopHide() {
        handlerTask.removeMessages(0)
    }

}