package org.glimpseframework.android.hologram;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.content.res.TypedArray;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import java.util.EnumMap;
import java.util.Map;

/**
 * Glimpse Hologram view for Android application.
 *
 * @author Sławomir Czerwiński
 */
public class GlimpseHoloView extends GLSurfaceView {

	public GlimpseHoloView(Context context) {
		super(context);
		applyDefaultAttributes(context);
		init(context);
	}

	private void applyDefaultAttributes(Context context) {
		RawResourceLoader loader = new RawResourceLoader(context);
		vertexShaderSource = loader.loadResource(R.raw.glimpse_holo_vertex);
		fragmentShaderSource = loader.loadResource(R.raw.glimpse_holo_fragment);
	}

	private void init(Context context) {
		this.context = context;
		if (isInEditMode()) return;
		final ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		final ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
		final boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000;
		if (supportsEs2) {
			setZOrderOnTop(true);
			setEGLContextClientVersion(2);
			setEGLConfigChooser(8, 8, 8, 8, 16, 0);
			getHolder().setFormat(PixelFormat.TRANSLUCENT);
			accelerometer = new Accelerometer(context);
			setRenderer(new GlimpseHoloRenderer(createShaderProgram(), createTexturesStore(), accelerometer));
		} else {
			throw new UnsupportedOperationException("OpenGL ES 2.0 not supported.");
		}
	}

	private ShaderProgram createShaderProgram() {
		Map<Shader.ShaderType, String> shaderSources =
				new EnumMap<Shader.ShaderType, String>(Shader.ShaderType.class);

		shaderSources.put(Shader.ShaderType.VERTEX_SHADER, vertexShaderSource);
		shaderSources.put(Shader.ShaderType.FRAGMENT_SHADER, fragmentShaderSource);

		return new ShaderProgram(shaderSources);
	}

	private TexturesStore createTexturesStore() {
		Map<Texture.TextureType, Integer> textureResources =
				new EnumMap<Texture.TextureType, Integer>(Texture.TextureType.class);

		textureResources.put(Texture.TextureType.BACKGROUND_TEXTURE, R.drawable.background);
		textureResources.put(Texture.TextureType.HOLOGRAM_TEXTURE, R.drawable.hologram);
		textureResources.put(Texture.TextureType.HOLO_MAP_TEXTURE, R.drawable.holo_map);

		return new TexturesStore(context, textureResources);
	}

	public GlimpseHoloView(Context context, AttributeSet attrs) {
		super(context, attrs);
		applyAttributes(context, attrs);
		init(context);
	}

	private void applyAttributes(Context context, AttributeSet attrs) {
		TypedArray attributes = context.getTheme().obtainStyledAttributes(attrs, R.styleable.GlimpseHoloView, 0, 0);
		RawResourceLoader loader = new RawResourceLoader(context);
		vertexShaderSource = loader.loadResource(attributes.getResourceId(R.styleable.GlimpseHoloView_vertexShader, R.raw.glimpse_holo_vertex));
		fragmentShaderSource = loader.loadResource(attributes.getResourceId(R.styleable.GlimpseHoloView_fragmentShader, R.raw.glimpse_holo_fragment));
		attributes.recycle();
	}

	@Override
	public void onResume() {
		super.onResume();
		accelerometer.register();
	}

	@Override
	public void onPause() {
		super.onPause();
		accelerometer.unregister();
	}

	private Context context;

	private String vertexShaderSource;
	private String fragmentShaderSource;

	private Accelerometer accelerometer;
}
