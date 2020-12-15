package com.nmd.volume

import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.os.*
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.SeekBar
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.Nullable
import androidx.appcompat.widget.AppCompatSeekBar
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.ViewCompat
import androidx.core.widget.ImageViewCompat


/**
 * This class will create a new Volume Control View object.
 * You can add the class direct in your xml layout with follwing properties:
 * - animate_show_from_right_to_left | boolean
 * - volume_start_positon | integer
 * - volume_thumb_color | color
 * - volume_thumb_progress_color | color
 * - volume_icon_color | color
 */
@Suppress("MemberVisibilityCanBePrivate", "unused")
class VolumeControlView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr) {
    private var appCompatSeekBar: AppCompatSeekBar? = null
    private var imageView: ImageView? = null
    private var extraMargin = 0f
    private val handlerTask = Handler(Looper.getMainLooper())
    private var musicOn = true

    @ColorInt
    private var volumeThumbColor = 0

    @ColorInt
    private var volumeThumbProgressColor = 0

    @ColorInt
    private var volumeIconColor = 0

    private var initShow = true
    private var animateShowFromRightToLeft = true
    private var volumeStartPosition = 50
    private var isCurrentVisible = initShow
    private var listenerView: OnVolumeControlViewChangeListener? = null
    private val displayMetrics = DisplayMetrics()

    /**
     * Returns true if the volume control view is currently shown.
     */
    fun show(): Boolean {
        return initShow
    }

    /**
     * Show or hide the volume control view.
     * @param show Boolean
     */
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
     * Set the start position of the volume control view seekbar.
     * @param position Int
     */
    fun startPosition(position: Int) {
        volumeStartPosition = position
        appCompatSeekBar?.progress = position
    }

    /**
     * Get the start position of the volume control view seekbar.
     */
    fun startPosition(): Int {
        return volumeStartPosition
    }

    /**
     * Sets the volume thumb color for this volume control view.
     * @param thumbColor Int
     */
    fun setThumbColor(thumbColor: Int) {
        volumeThumbColor = thumbColor
        setColors()
    }

    /**
     * Sets the volume thumb color resource for this volume control view.
     * @param thumbColor ColorRes
     */
    fun setThumbColorResource(@ColorRes thumbColor: Int) {
        volumeThumbColor = ContextCompat.getColor(context, thumbColor)
        setColors()
    }

    /**
     * Gets the volume thumb color for this volume control view.
     */
    fun getThumbColor(): Int {
        return volumeThumbColor
    }

    /**
     * Sets the volume thumb progress color for this volume control view.
     * @param thumbProgressColor Int
     */
    fun setThumbProgressColor(thumbProgressColor: Int) {
        volumeThumbProgressColor = thumbProgressColor
        setColors()
    }

    /**
     * Sets the volume thumb progress color resource for this volume control view.
     * @param thumbProgressColor ColorRes
     */
    fun setThumbProgressColorResource(@ColorRes thumbProgressColor: Int) {
        volumeThumbProgressColor = ContextCompat.getColor(context, thumbProgressColor)
        setColors()
    }

    /**
     * Gets the volume thumb progress color for this volume control view.
     */
    fun getThumbProgressColor(): Int {
        return volumeThumbProgressColor
    }

    /**
     * Sets the volume icon color for this volume control view.
     * @param iconColor Int
     */
    fun setIconColor(iconColor: Int) {
        volumeIconColor = iconColor
        setColors()
    }

    /**
     * Sets the volume icon color resource for this volume control view.
     * @param iconColor ColorRes
     */
    fun setIconColorResource(@ColorRes iconColor: Int) {
        volumeIconColor = ContextCompat.getColor(context, iconColor)
        setColors()
    }

    /**
     * Gets the volume icon color for this volume control view.
     */
    fun getIconColor(): Int {
        return volumeIconColor
    }

    /**
     * The interface to listen to volume control view changes.
     */
    interface OnVolumeControlViewChangeListener {
        fun onSeekBarChange(position: Int)
        fun onShowChange(visible: Boolean)
    }

    /**
     * Set the listener for the volume control view.
     * @param onVolumeControlViewChangeListener OnVolumeControlViewChangeListener
     */
    fun setOnVolumeControlViewChangeListener(@Nullable onVolumeControlViewChangeListener: OnVolumeControlViewChangeListener?) {
        listenerView = onVolumeControlViewChangeListener
    }

    /**
     * Delete the listener from the volume control view.
     */
    fun deleteOnVolumeControlViewChangeListener() {
        listenerView = null
    }

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            (context as Activity).display?.getRealMetrics(displayMetrics)
        } else {
            @Suppress("DEPRECATION")
            (context as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
        }
        ViewCompat.setBackground(this, ContextCompat.getDrawable(context, R.drawable.round_background))
        ViewCompat.setElevation(this, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4f, resources.displayMetrics))

        val view: View? = LayoutInflater.from(context).inflate(R.layout.volume_control_view, context.findViewById(android.R.id.content), false)
        view?.let {
            addView(it)
        }

        if (attrs != null) {
            context.theme.obtainStyledAttributes(attrs, R.styleable.VolumeControlView, defStyleAttr, 0).apply {
                try {
                    initShow = try {
                        getBoolean(R.styleable.VolumeControlView_show, true)
                    } catch (e: Exception) {
                        true
                    }
                    animateShowFromRightToLeft = try {
                        getBoolean(R.styleable.VolumeControlView_animate_show_from_right_to_left, true)
                    } catch (e: Exception) {
                        true
                    }
                    volumeStartPosition = try {
                        getInteger(R.styleable.VolumeControlView_volume_start_positon, 50)
                    } catch (e: Exception) {
                        50
                    }
                    volumeThumbColor = try {
                        getColor(R.styleable.VolumeControlView_volume_thumb_color, ContextCompat.getColor(context, R.color.thumb_color))
                    } catch (e: Exception) {
                        ContextCompat.getColor(context, R.color.thumb_color)
                    }
                    volumeThumbProgressColor = try {
                        getColor(R.styleable.VolumeControlView_volume_thumb_progress_color, ContextCompat.getColor(context, R.color.thumb_progress_color))
                    } catch (e: Exception) {
                        ContextCompat.getColor(context, R.color.thumb_progress_color)
                    }
                    volumeIconColor = try {
                        getColor(R.styleable.VolumeControlView_volume_icon_color, ContextCompat.getColor(context, R.color.icon_color))
                    } catch (e: Exception) {
                        ContextCompat.getColor(context, R.color.icon_color)
                    }
                } catch (e: Exception) {
                    Log.e("VCV", "Volume control view was not able to load styles from xml.")
                } finally {
                    recycle()
                }
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

    private fun setListeners() {
        var lastPosition: Int = appCompatSeekBar?.progress ?: 50
        val ctx = context ?: return
        appCompatSeekBar?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (progress == 0) {
                    musicOn = false
                    imageView?.setImageDrawable(ContextCompat.getDrawable(ctx, R.drawable.music_off))
                } else {
                    if (!musicOn) {
                        imageView?.setImageDrawable(ContextCompat.getDrawable(ctx, R.drawable.music_on))
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
                imageView?.setImageDrawable(ContextCompat.getDrawable(ctx, R.drawable.music_off))
            } else {
                animateProgressChange(false, lastPosition)
                imageView?.setImageDrawable(ContextCompat.getDrawable(ctx, R.drawable.music_on))
            }
            timerStopHide()
            timerStartHide()
        }
    }

    private fun muteAppCompatSeekBar() {
        val ctx = context ?: return
        musicOn = false
        imageView?.setImageDrawable(ContextCompat.getDrawable(ctx, R.drawable.music_off))
    }

    private fun animateProgressChange(musicOff: Boolean, lastPosition: Int) {
        appCompatSeekBar?.let {
            val anim: ObjectAnimator = if (musicOff) {
                ObjectAnimator.ofInt(it, "progress", it.progress, 0)
            } else {
                ObjectAnimator.ofInt(it, "progress", 0, lastPosition)
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
        appCompatSeekBar?.let {
            DrawableCompat.setTint(it.thumb, volumeThumbColor)
            DrawableCompat.setTint(it.progressDrawable, volumeThumbProgressColor)
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
        return resources?.configuration?.orientation == Configuration.ORIENTATION_PORTRAIT
    }

}