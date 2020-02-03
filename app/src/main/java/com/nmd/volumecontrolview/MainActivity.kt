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
    private var volumeControlView: VolumeControlView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        button = findViewById(R.id.button)
        textView = findViewById(R.id.textView)
        volumeControlView = findViewById(R.id.volumeControlView)

        button?.setOnClickListener {
            volumeControlView?.let {
                it.show(!it.show())
            }
        }

        volumeControlView?.setOnVolumeControlChangeListener(object :
            VolumeControlView.OnVolumeControlChangeListener {
            override fun onChange(position: Int) {
                textView?.text = "Progress: $position"
            }
        })
    }

}
