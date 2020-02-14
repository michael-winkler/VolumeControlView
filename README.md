# VolumeControlView

#### Description:    
The library "Volume Control View" will provide you a simple volume control view widget like Pixel phones from Google use it.

| Pixel    | Volume Control View |
| ---      | ---      |
| ![Image description](https://github.com/Mika-89/VolumeControlView/blob/master/images/image_pixel.png) | ![Image description](https://github.com/Mika-89/VolumeControlView/blob/master/images/image_library.png) |


#### Xml:    
```xml
<com.nmd.volume.VolumeControlView
    android:id="@+id/volumeControlView"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:layout_alignParentEnd="true"
    android:layout_alignParentRight="true"
    android:layout_centerInParent="true"
    android:layout_margin="8dp"
    app:animate_show_from_right_to_left="true"
    app:show="true"
    app:volume_start_positon="50" />
```

Available xml options:   
app:volume_start_positon=""
app:animate_show_from_right_to_left=""
app:volume_icon_color=""
app:volume_thumb_color=""
app:volume_thumb_progress_color=""
