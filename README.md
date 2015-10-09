[![Build Status](https://travis-ci.org/GlimpseFramework/glimpse-android-hologram.svg?branch=master)](https://travis-ci.org/GlimpseFramework/glimpse-android-hologram)

# GlimpseFramework Hologram View

GlimpseFramework Hologram View provides an easy way of placing 2D holograms in Android applications.

## Usage

First, add `GlimpseHoloView` to your layout:
```xml
<org.glimpseframework.android.hologram.GlimpseHoloView
	android:id="@+id/holo_view"
	android:layout_width="128dp"
	android:layout_height="128dp"
	android:layout_centerHorizontal="true"
	android:layout_centerVertical="true"/>
```

In your activity, override `onResume()` and `onPause()` to call methods from `GlimpseHoloView`:
```java
@Override
protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	glimpseHoloView = (GlimpseHoloView) findViewById(R.id.holo_view);
}

@Override
protected void onResume() {
	super.onResume();
	glimpseHoloView.onResume();
}

@Override
protected void onPause() {
	super.onPause();
	glimpseHoloView.onPause();
}
```

And finally, add these features to your `AndroidManifest.xml`:
```xml
<uses-feature android:glEsVersion="0x00020000" android:required="true" />
<uses-feature android:name="android.hardware.sensor.accelerometer" android:required="true" />
```

### Using Custom Textures

To apply custom textures, add the following attributes to your `GlimpseHoloView`:
```xml
<org.glimpseframework.android.hologram.GlimpseHoloView
	xmlns:app="http://schemas.android.com/apk/res-auto"
	app:backgroundTexture="@drawable/my_background"
	app:hologramTexture="@drawable/my_hologram"
	app:holoMapTexture="@drawable/my_holo_map"/>
```
