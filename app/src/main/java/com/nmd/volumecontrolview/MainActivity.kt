package com.nmd.volumecontrolview

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.nmd.volume.VolumeControlView

class MainActivity : AppCompatActivity() {
    private var button: Button? = null
    private var volumeControlView: VolumeControlView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        button = findViewById(R.id.button)
        volumeControlView = findViewById(R.id.volumeControlView)

        button?.setOnClickListener {
            volumeControlView?.let {
                it.show(!it.show())
            }
        }
    }

}
