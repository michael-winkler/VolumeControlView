package com.nmd.volume


import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.TypedValue
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.SeekBar
import androidx.appcompat.widget.AppCompatSeekBar
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.ViewCompat
import androidx.core.widget.ImageViewCompat


@SuppressLint("ResourceAsColor")
class VolumeControlView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var appCompatSeekBar: AppCompatSeekBar? = null
    private var imageView: ImageView? = null
    private var extraMargin = 0f
    private val handlerTask = Handler(Looper.getMainLooper())
    private var musicOn = true
    private var volumeThumbColor = R.color.thumb_color
    private var volumeThumbProgressColor = R.color.thumb_progress_color
    private var volumeIconColor = R.color.icon_color
    private var initShow = true
    private var animateShowFromRightToLeft = true
    private var volumeStartPosition = 50
    var listener: OnVolumeControlChangeListener? = null

    @Suppress("MemberVisibilityCanBePrivate")
    fun show(): Boolean {
        return initShow
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun show(show: Boolean) {
        initShow = show
        if (initShow) {
            timerStartHide()
        } else {
            timerStopHide()
        }
        animateViewVisibility(initShow)
    }

    /**
     * Show or hide the volume control view.
     */
    @Suppress("MemberVisibilityCanBePrivate")
    var startPosition: Int
        get() = volumeStartPosition
        set(position) {
            volumeStartPosition = position
            appCompatSeekBar?.progress = volumeStartPosition
        }

    @Suppress("MemberVisibilityCanBePrivate")
    interface OnVolumeControlChangeListener {
        fun onChange(position: Int)
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun setOnVolumeControlChangeListener(onVolumeControlChangeListener: OnVolumeControlChangeListener) {
        listener = onVolumeControlChangeListener
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
                initShow = a.getBoolean(R.styleable.VolumeControlView_show, initShow)
                animateShowFromRightToLeft = a.getBoolean(
                    R.styleable.VolumeControlView_animate_show_from_right_to_left,
                    animateShowFromRightToLeft
                )
                volumeStartPosition =
                    a.getInteger(
                        R.styleable.VolumeControlView_volume_start_positon,
                        volumeStartPosition
                    )
                volumeThumbColor = a.getColor(
                    R.styleable.VolumeControlView_volume_thumb_color,
                    ContextCompat.getColor(context, R.color.thumb_color)
                )
                volumeThumbProgressColor = a.getColor(
                    R.styleable.VolumeControlView_volume_thumb_progress_color,
                    ContextCompat.getColor(context, R.color.thumb_progress_color)
                )
                volumeIconColor = a.getColor(
                    R.styleable.VolumeControlView_volume_icon_color,
                    ContextCompat.getColor(context, R.color.icon_color)
                )
            } finally {
                a.recycle()
            }
        }

        if (!initShow) {
            x =
                TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    getSideFloatValue(),
                    resources.displayMetrics
                )
        } else {
            timerStartHide()
        }

        appCompatSeekBar = findViewById(R.id.seekbar_view)
        imageView = findViewById(R.id.image_view)

        appCompatSeekBar?.progress = volumeStartPosition
        if (volumeStartPosition == 0) {
            muteAppCompatSeekBar()
        }

        setColors()
        setListeners()

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

    private fun setListeners() {
        var lastPosition: Int = appCompatSeekBar?.progress ?: 50
        context?.let {

            appCompatSeekBar?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    if (progress == 0) {
                        musicOn = false
                        imageView?.setImageDrawable(
                            ContextCompat.getDrawable(
                                it,
                                R.drawable.music_off
                            )
                        )

                    } else {
                        if (!musicOn) {
                            imageView?.setImageDrawable(
                                ContextCompat.getDrawable(
                                    it,
                                    R.drawable.music_on
                                )
                            )
                        }
                        musicOn = true
                    }
                    listener?.onChange(progress)
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                    timerStopHide()
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    timerStartHide()
                }
            })

            imageView?.setOnClickListener {
                if (musicOn) {
                    lastPosition = appCompatSeekBar?.progress ?: 50
                    animateProgressChange(true, lastPosition)
                    imageView?.setImageDrawable(
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.music_off
                        )
                    )
                } else {
                    animateProgressChange(false, lastPosition)
                    imageView?.setImageDrawable(
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.music_on
                        )
                    )
                }
                timerStopHide()
                timerStartHide()
            }
        }

    }

    private fun muteAppCompatSeekBar() {
        context?.let {
            musicOn = false
            imageView?.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.music_off))
        }
    }

    @SuppressLint("ObjectAnimatorBinding")
    private fun animateProgressChange(musicOff: Boolean, lastPosition: Int) {
        appCompatSeekBar?.let {
            val anim: ObjectAnimator = if (musicOff) {
                ObjectAnimator.ofInt(appCompatSeekBar, "progress", it.progress, 0)
            } else {
                ObjectAnimator.ofInt(appCompatSeekBar, "progress", 0, lastPosition)
            }

            anim.duration = 300
            anim.start()
        }
    }

    private fun setColors() {
        context?.let {
            appCompatSeekBar?.let {
                DrawableCompat.setTint(
                    it.thumb, volumeThumbColor
                )
                DrawableCompat.setTint(
                    it.progressDrawable, volumeThumbProgressColor
                )
            }
        }
        imageView?.let {
            ImageViewCompat.setImageTintList(
                it,
                ColorStateList.valueOf(volumeIconColor)
            )
        }
    }

    private fun animateViewVisibility(visible: Boolean) {
        if (!visible) {
            animate().translationX(
                TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    getSideFloatValue(),
                    resources.displayMetrics
                )
            )
        } else {
            animate().translationX(0f)
        }
    }

    private fun getSideFloatValue(): Float {
        return if (animateShowFromRightToLeft) {
            56f.plus(extraMargin)
        } else {
            (-56f).minus(-extraMargin)
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