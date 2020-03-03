# VolumeControlView

[![API](https://img.shields.io/badge/API-16%2B-orange.svg?style=flat)](https://android-arsenal.com/api?level=16)
[![License Apache 2.0](https://img.shields.io/badge/License-Apache%202.0-blue.svg?style=true)](http://www.apache.org/licenses/LICENSE-2.0)

#### Description:    
The library "Volume Control View" will provide you a simple volume control view widget like Pixel phones from Google use it.

| Pixel    | Volume Control View |
| ---      | ---      |
| ![Image description](https://github.com/Mika-89/VolumeControlView/blob/master/images/image_pixel.png) | ![Image description](https://github.com/Mika-89/VolumeControlView/blob/master/images/image_library.png) |

## Usage
Add a dependency to your build.gradle file:
```java
allprojects {
    repositories {
	    ...
	    maven { url 'https://jitpack.io' }
    }
}

dependencies {
    implementation 'com.github.Mika-89:VolumeControlView:master-SNAPSHOT'
}
```

```java
import com.nmd.volume.VolumeControlView;
```


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
| parameter    | type | example | description |
| ---          | ---  |  ---    | ---         |
| app:volume_start_positon="" | Integer |  app:volume_start_positon="50" | Set the start position for the thumb |
| app:animate_show_from_right_to_left="" | Boolean | app:animate_show_from_right_to_left="true" | If true the start animation will be from screen right to left |
| app:volume_icon_color="" | Color | app:volume_icon_color="#fff" | Set the color for the volume icon. |
| app:volume_thumb_color="" | Color | app:volume_thumb_color="#fff" | Set the color for thumb background. |
| app:volume_thumb_progress_color="" | Color | app:volume_thumb_progress_color="#fff" | Set the color for thumb progress. |

#### Methods: (Kotlin)   
| name    | type | example | description |
| ---     | ---  |  ---    | ---         |
| show()  | Boolean | volumeControlView.show() | Returns true if the volume control view is currently shown. |
| show(show: Boolean)  | fun | volumeControlView.show(true) | Show or hide the volume control view. |
| startPosition()  | Int | volumeControlView.startPosition() | Get the start position of the volume control view seekbar. |
| startPosition(position: Int)  | fun | volumeControlView.startPosition(60) | Set the start position of the volume control view seekbar. |

##### Interface
| name    | type | example | description |
| ---     | ---  |  ---    | ---         |
| setOnVolumeControlViewChangeListener(onVolumeControlViewChangeListener: OnVolumeControlViewChangeListener?)  | OnVolumeControlViewChangeListener | volumeControlView.setOnVolumeControlViewChangeListener(onVolumeControlViewChangeListener) | Set the listener for the volume control view. |

#### View the library
| Show/Hide    | Change Volume |
| ---      | ---      |
| ![Image description](https://github.com/Mika-89/VolumeControlView/blob/master/images/animation1.gif) | ![Image description](https://github.com/Mika-89/VolumeControlView/blob/master/images/animation2.gif) |

#### Known bugs/issues
You can find [here](https://github.com/Mika-89/VolumeControlView/issues) the known bugs/issues
