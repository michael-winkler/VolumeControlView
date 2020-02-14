package com.nmd.volumecontrolview

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.nmd.volume.VolumeControlView

@SuppressLint("SetTextI18n")
class MainActivity : AppCompatActivity() {
    private var button: Button? = null
    private var textView: TextView? = null
    private var textView2: TextView? = null
    private var volumeControlView: VolumeControlView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        button = findViewById(R.id.button)
        textView = findViewById(R.id.textView)
        textView2 = findViewById(R.id.textView2)
        volumeControlView = findViewById(R.id.volumeControlView)

        button?.setOnClickListener {
            volumeControlView?.let {
                it.show(!it.show())
            }
        }

        volumeControlView?.setOnVolumeControlViewChangeListener(object : VolumeControlView.OnVolumeControlViewChangeListener {
            override fun onSeekBarChange(position: Int) {
                textView?.text = "Progress: $position"
            }

            override fun onShowChange(visible: Boolean) {
                textView2?.text = "Control View Visible: $visible"
            }
        })
    }

}
