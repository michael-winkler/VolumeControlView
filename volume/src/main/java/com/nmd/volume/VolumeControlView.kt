package com.nmd.volume

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Parcelable
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.SeekBar
import androidx.appcompat.widget.AppCompatSeekBar
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.ViewCompat
import androidx.core.widget.ImageViewCompat


@SuppressLint("ResourceAsColor")
class VolumeControlView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr) {

    private var appCompatSeekBar: AppCompatSeekBar? = null
    private var imageView: ImageView? = null
    private var extraMargin = 0f
    private val handlerTask = Handler(Looper.getMainLooper())
    private var musicOn = true
	//@ColorInt
    private var volumeThumbColor = R.color.thumb_color
    private var volumeThumbProgressColor = R.color.thumb_progress_color
    private var volumeIconColor = R.color.icon_color
    private var initShow = true
    private var animateShowFromRightToLeft = true
    private var volumeStartPosition = 50
    private var isCurrentVisible = initShow
    private var listenerView: OnVolumeControlViewChangeListener? = null
    private val displayMetrics = DisplayMetrics()

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
    interface OnVolumeControlViewChangeListener {
        fun onSeekBarChange(position: Int)
        fun onShowChange(visible: Boolean)
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun setOnVolumeControlViewChangeListener(onVolumeControlViewChangeListener: OnVolumeControlViewChangeListener) {
        listenerView = onVolumeControlViewChangeListener
    }

    init {
        (context as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
        ViewCompat.setBackground(this, ContextCompat.getDrawable(context, R.drawable.round_background))
        ViewCompat.setElevation(this, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4f, resources.displayMetrics))
        addView(inflate(context, R.layout.volume_control_view, null))

        if (attrs != null) {
            val typedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.VolumeControlView, defStyleAttr, 0)

            try {
                initShow = typedArray.getBoolean(R.styleable.VolumeControlView_show, true)
                animateShowFromRightToLeft = typedArray.getBoolean(R.styleable.VolumeControlView_animate_show_from_right_to_left, true)
                volumeStartPosition = typedArray.getInteger(R.styleable.VolumeControlView_volume_start_positon, 50)
                volumeThumbColor = typedArray.getColor(R.styleable.VolumeControlView_volume_thumb_color, ContextCompat.getColor(context, R.color.thumb_color))
                volumeThumbProgressColor = typedArray.getColor(R.styleable.VolumeControlView_volume_thumb_progress_color, ContextCompat.getColor(context, R.color.thumb_progress_color))
                volumeIconColor = typedArray.getColor(R.styleable.VolumeControlView_volume_icon_color, ContextCompat.getColor(context, R.color.icon_color))
            } finally {
                typedArray.recycle()
            }
        }

        if (!initShow) {
            visibility = View.GONE
            post {
                x = if (animateShowFromRightToLeft) {
                    displayMetrics.widthPixels.toFloat()
                } else {
                    0f
                }
                visibility = View.VISIBLE
            }
        } else {
            timerStartHide()
        }

        appCompatSeekBar = findViewById(R.id.seekbar_view)
        imageView = findViewById(R.id.image_view)

        appCompatSeekBar?.progress = volumeStartPosition
        if (volumeStartPosition == 0) {
            muteAppCompatSeekBar()
        }

        setHeight()
        setColors()
        setListeners()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        // If portrait mode:
        // Control view height is 30 % of device height. If landscape 50 % of device height
        val height = displayMetrics.heightPixels.div(100).times(if (isPortraitMode()) 30 else 50)
        val width: Int = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 56f, resources.displayMetrics).toInt()
        super.onMeasure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY))
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        val marginLayoutParams = MarginLayoutParams::class.java.cast(layoutParams)
        val marginRight = marginLayoutParams?.rightMargin ?: 0
        extraMargin = marginRight / (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    }


    override fun onSaveInstanceState(): Parcelable {
        val state = Bundle()

        state.putBoolean("initShow", initShow)
        state.putBoolean("isCurrentVisible", isCurrentVisible)
        state.putBoolean("animateShowFromRightToLeft", animateShowFromRightToLeft)
        state.putParcelable("superState", super.onSaveInstanceState())
        return state
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        var viewState = state
        if (viewState is Bundle) {
            val initShow = viewState.getBoolean("initShow", true)
            val isCurrentVisible = viewState.getBoolean("isCurrentVisible", initShow)
            val animateShowFromRightToLeft = viewState.getBoolean("animateShowFromRightToLeft", true)
            this.initShow = initShow
            this.isCurrentVisible = isCurrentVisible
            this.animateShowFromRightToLeft = animateShowFromRightToLeft
            if (isCurrentVisible) {
                //TODO ?
            } else {
                visibility = View.GONE
                post {
                    x = if (animateShowFromRightToLeft) {
                        displayMetrics.widthPixels.toFloat()
                    } else {
                        0f
                    }
                    visibility = View.VISIBLE
                }

            }
            viewState = viewState.getParcelable("superState")
        }
        super.onRestoreInstanceState(viewState)
    }

    /*
    @Override
    public Bundle onSaveInstanceState() {
        final Bundle state = super.onSaveInstanceState();
        state.putInt(HOUR, mTimePicker.getCurrentHour());
        state.putInt(MINUTE, mTimePicker.getCurrentMinute());
        state.putBoolean(IS_24_HOUR, mTimePicker.is24HourView());
        return state;
    }
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        final int hour = savedInstanceState.getInt(HOUR);
        final int minute = savedInstanceState.getInt(MINUTE);
        mTimePicker.setIs24HourView(savedInstanceState.getBoolean(IS_24_HOUR));
        mTimePicker.setCurrentHour(hour);
        mTimePicker.setCurrentMinute(minute);
    }
     */

    private fun setListeners() {
        var lastPosition: Int = appCompatSeekBar?.progress ?: 50
        context?.let {
            appCompatSeekBar?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    if (progress == 0) {
                        musicOn = false
                        imageView?.setImageDrawable(ContextCompat.getDrawable(it, R.drawable.music_off))
                    } else {
                        if (!musicOn) {
                            imageView?.setImageDrawable(ContextCompat.getDrawable(it, R.drawable.music_on))
                        }
                        musicOn = true
                    }
                    listenerView?.onSeekBarChange(progress)
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
                    imageView?.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.music_off))
                } else {
                    animateProgressChange(false, lastPosition)
                    imageView?.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.music_on))
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

    private fun setHeight() {
        appCompatSeekBar?.let {
            val height = displayMetrics.heightPixels.div(100).times(if (isPortraitMode()) 20 else 35)
            val params = it.layoutParams
            params.width = height
        }
    }

    private fun setColors() {
        context?.let {
            appCompatSeekBar?.let {
                DrawableCompat.setTint(it.thumb, volumeThumbColor)
                DrawableCompat.setTint(it.progressDrawable, volumeThumbProgressColor)
            }
        }
        imageView?.let {
            ImageViewCompat.setImageTintList(it, ColorStateList.valueOf(volumeIconColor))
        }
    }

    private fun animateViewVisibility(visible: Boolean) {
        if (!visible) {
            isCurrentVisible = false
            animate().translationX(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, getSideFloatValue(), resources.displayMetrics)).withEndAction {
                listenerView?.onShowChange(false)
            }
        } else {
            isCurrentVisible = true
            animate().translationX(0f).withEndAction {
                listenerView?.onShowChange(true)
            }
        }
    }

    private fun getSideFloatValue(): Float {
        return if (animateShowFromRightToLeft) {
            56f.plus(extraMargin)
        } else {
            (-56f).plus(-extraMargin)
        }
    }

    private fun timerStartHide() {
        handlerTask.postDelayed({
            show(false)
        }, 2700)
    }

    private fun timerStopHide() {
        handlerTask.removeMessages(0)
    }

    private fun isPortraitMode(): Boolean {
        return resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
    }

}