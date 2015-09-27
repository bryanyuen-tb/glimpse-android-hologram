# GlimpseFramework Android Hologram

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

