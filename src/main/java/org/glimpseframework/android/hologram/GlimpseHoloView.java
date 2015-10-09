package org.glimpseframework.android.hologram;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

/**
 * GlimpseFramework Hologram view for Android application.
 *
 * @author Sławomir Czerwiński
 */
public class GlimpseHoloView extends GLSurfaceView {

	/**
	 * Standard {@link android.view.View View} constructor.
	 *
	 * @param context The context the hologram view is running in.
	 */
	public GlimpseHoloView(Context context) {
		this(context, null);
	}

	/**
	 * Standard {@link android.view.View View} constructor used when an XML tag is inflating the view.
	 *
	 * @param context The context the hologram view is running in.
	 * @param attrs The attributes of the XML tag that is inflating the view.
	 */
	public GlimpseHoloView(Context context, AttributeSet attrs) {
		super(context, attrs);
		config = new GlimpseHoloConfig(context, attrs);
		initView(context);
	}

	private void initView(Context context) {
		if (!isInEditMode()) {
			accelerometer = new Accelerometer(context);
			if (isGLES20Supported(context)) {
				initGLES();
			} else {
				throw new UnsupportedOperationException("OpenGL ES 2.0 not supported.");
			}
		}
	}

	private boolean isGLES20Supported(Context context) {
		final ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		return activityManager.getDeviceConfigurationInfo().reqGlEsVersion >= 0x20000;
	}

	private void initGLES() {
		setZOrderOnTop(true);
		setEGLContextClientVersion(2);
		setEGLConfigChooser(8, 8, 8, 8, 0, 0);
		getHolder().setFormat(PixelFormat.TRANSLUCENT);
		setRenderer(new GlimpseHoloRenderer(config, accelerometer));
	}

	/**
	 * Informs the hologram view that the activity is resumed.
	 *
	 * @see GLSurfaceView#onResume()
	 */
	@Override
	public void onResume() {
		super.onResume();
		accelerometer.register();
	}

	/**
	 * Informs the hologram view that the activity is paused.
	 *
	 * @see GLSurfaceView#onResume()
	 */
	@Override
	public void onPause() {
		super.onPause();
		accelerometer.unregister();
	}

	private GlimpseHoloConfig config;

	private Accelerometer accelerometer;
}
