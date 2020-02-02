package com.nmd.volume.util

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.ImageView
import android.widget.SeekBar
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.nmd.volume.R


class VolumeUtil {
    companion object {
        var musicOn = true

        fun setListeners(context: Context?, seekBar: SeekBar?, imageView: ImageView?) {
            var lastPosition: Int = seekBar?.progress ?: 50
            context?.let {

                val tintColor = ContextCompat.getColor(it, R.color.image_tint_color)
                val musicOnDrawable: Drawable? = ContextCompat.getDrawable(it, R.drawable.music_on)
                val musicOffDrawable: Drawable? =
                    ContextCompat.getDrawable(it, R.drawable.music_off)

                if (musicOnDrawable != null) {
                    DrawableCompat.setTint(musicOnDrawable, tintColor)
                }

                if (musicOffDrawable != null) {
                    DrawableCompat.setTint(musicOffDrawable, tintColor)
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
                }
            }

        }

        fun muteSeekbar(imageView: ImageView?, context: Context?) {
            context?.let {
                musicOn = false
                val musicOffDrawable: Drawable? = ContextCompat.getDrawable(context, R.drawable.music_off)
                val tintColor = ContextCompat.getColor(context, R.color.image_tint_color)
                if (musicOffDrawable != null) {
                    DrawableCompat.setTint(musicOffDrawable, tintColor)
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
                        it.thumb,
                        ContextCompat.getColor(context, R.color.thumb_color)
                    )
                    DrawableCompat.setTint(
                        it.progressDrawable,
                        ContextCompat.getColor(context, R.color.thumb_background_color)
                    )
                }

                imageView?.let {
                    DrawableCompat.setTint(
                        it.drawable,
                        ContextCompat.getColor(context, R.color.image_tint_color)
                    )
                }
            }
        }
    }
}