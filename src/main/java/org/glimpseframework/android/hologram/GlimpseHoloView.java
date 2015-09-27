package org.glimpseframework.android.hologram;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.content.res.TypedArray;
import android.graphics.PixelFormat;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.AttributeSet;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Glimpse Hologram view for Android application.
 *
 * @author Sławomir Czerwiński
 */
public class GlimpseHoloView extends GLSurfaceView implements SensorEventListener {

	private class GlimpseHoloRenderer implements Renderer {

		public GlimpseHoloRenderer() {
			final float[] vertices = {
					-1.0f, -1.0f, 0.0f,
					1.0f, -1.0f, 0.0f,
					-1.0f, 1.0f, 0.0f,
					-1.0f, 1.0f, 0.0f,
					1.0f, -1.0f, 0.0f,
					1.0f, 1.0f, 0.0f,
			};
			final float[] textureCoordinates = {
					0.0f, 1.0f,
					1.0f, 1.0f,
					0.0f, 0.0f,
					0.0f, 0.0f,
					1.0f, 1.0f,
					1.0f, 0.0f,
			};
			verticesBuffer = ByteBuffer
					.allocateDirect(vertices.length * BYTES_PER_FLOAT)
					.order(ByteOrder.nativeOrder())
					.asFloatBuffer()
					.put(vertices);
			textureCoordinatesBuffer = ByteBuffer
					.allocateDirect(textureCoordinates.length * BYTES_PER_FLOAT)
					.order(ByteOrder.nativeOrder())
					.asFloatBuffer()
					.put(textureCoordinates);
		}

		@Override
		public void onSurfaceCreated(GL10 gl, EGLConfig config) {
			GLES20.glEnable(GLES20.GL_BLEND);
			GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

			GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
			Matrix.orthoM(mvpMatrix, 0, -1.0f, 1.0f, -1.0f, 1.0f, -1.0f, 1.0f);

			initShaders();
			initTextures();
		}

		private void initShaders() {
			ShaderProgram program = new ShaderProgram(
					new Shader(Shader.ShaderType.VERTEX_SHADER, vertexShaderSource),
					new Shader(Shader.ShaderType.FRAGMENT_SHADER, fragmentShaderSource));

			mvpMatrixHandle = program.getUniformLocation("u_MVPMatrix");
			vertexPositionHandle = program.getAttribLocation("a_VertexPosition");
			textureCoordinatesHandle = program.getAttribLocation("a_TextureCoordinates");
			accelerometerXHandle = program.getUniformLocation("u_AccelerometerX");
			accelerometerYHandle = program.getUniformLocation("u_AccelerometerY");

			backgroundTextureHandle = program.getUniformLocation("u_BackgroundTexture");
			hologramTextureHandle = program.getUniformLocation("u_HologramTexture");
			holoMapTextureHandle = program.getUniformLocation("u_HoloMapTexture");

			program.use();
		}

		private void initTextures() {
			backgroundTexture = new Texture(context, R.drawable.background);
			hologramTexture = new Texture(context, R.drawable.hologram);
			holoMapTexture = new Texture(context, R.drawable.holo_map);

			backgroundTexture.bind(Texture.TextureType.BACKGROUND_TEXTURE, backgroundTextureHandle);
			hologramTexture.bind(Texture.TextureType.HOLOGRAM_TEXTURE, hologramTextureHandle);
			holoMapTexture.bind(Texture.TextureType.HOLO_MAP_TEXTURE, holoMapTextureHandle);
		}

		@Override
		public void onSurfaceChanged(GL10 gl, int width, int height) {
			GLES20.glViewport(0, 0, width, height);
		}

		@Override
		public void onDrawFrame(GL10 gl) {
			GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);

			verticesBuffer.position(0);
			textureCoordinatesBuffer.position(0);
			GLES20.glUniform1f(accelerometerXHandle, accelerometerX);
			GLES20.glUniform1f(accelerometerYHandle, accelerometerY);
			GLES20.glVertexAttribPointer(vertexPositionHandle, 3, GLES20.GL_FLOAT, false, 3 * BYTES_PER_FLOAT, verticesBuffer);
			GLES20.glEnableVertexAttribArray(vertexPositionHandle);
			GLES20.glVertexAttribPointer(textureCoordinatesHandle, 2, GLES20.GL_FLOAT, false, 2 * BYTES_PER_FLOAT, textureCoordinatesBuffer);
			GLES20.glEnableVertexAttribArray(textureCoordinatesHandle);
			GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0);
			GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, verticesBuffer.remaining());
		}

		private static final int BYTES_PER_FLOAT = 4;

		private final FloatBuffer verticesBuffer;
		private final FloatBuffer textureCoordinatesBuffer;

		private float[] mvpMatrix = new float[16];
		private Texture backgroundTexture;
		private Texture hologramTexture;
		private Texture holoMapTexture;

		private int mvpMatrixHandle;
		private int vertexPositionHandle;
		private int textureCoordinatesHandle;
		private int accelerometerXHandle;
		private int accelerometerYHandle;

		private int backgroundTextureHandle;
		private int hologramTextureHandle;
		private int holoMapTextureHandle;
	}

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
		final ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		final ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
		final boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000;
		if (supportsEs2) {
			setZOrderOnTop(true);
			setEGLContextClientVersion(2);
			setEGLConfigChooser(8, 8, 8, 8, 16, 0);
			getHolder().setFormat(PixelFormat.TRANSLUCENT);
			setRenderer(new GlimpseHoloRenderer());
		} else {
			throw new UnsupportedOperationException("OpenGL ES 2.0 not supported.");
		}
		if (isInEditMode()) return;
		sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
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
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			if (counter >= MAX_COUTER) {
				counter = 0;
			}
			xArray[counter] = event.values[0];
			yArray[counter] = event.values[1];
			zArray[counter] = event.values[2];
			counter ++;
			float x = 0.0f;
			float y = 0.0f;
			float z = 0.0f;
			for (int i = 0; i < MAX_COUTER; i++) {
				x += xArray[i];
				y += yArray[i];
				z += zArray[i];
			}
			float value = (float) Math.sqrt(x * x + y * y + z * z);
			accelerometerX = x / value;
			accelerometerY = y / value;
			accelerometerZ = z / value;
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	@Override
	public void onResume() {
		super.onResume();
		sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
	}

	@Override
	public void onPause() {
		super.onPause();
		sensorManager.unregisterListener(this);
	}

	private Context context;

	private String vertexShaderSource;
	private String fragmentShaderSource;

	private SensorManager sensorManager;
	private Sensor accelerometer;

	private static final int MAX_COUTER = 20;
	private float xArray[] = new float[MAX_COUTER];
	private float yArray[] = new float[MAX_COUTER];
	private float zArray[] = new float[MAX_COUTER];
	private int counter;
	private float accelerometerX;
	private float accelerometerY;
	private float accelerometerZ;
}
